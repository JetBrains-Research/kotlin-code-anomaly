CAP_OR_UNDER = frozenset(chr(i) for i in range(ord('A'), ord('Z') + 1)) | {'_'}


SKIP_PREFIXES = '<empty list>'
# TYPES_NOT_TO_EXPAND = {'LAMBDA_EXPRESSION', 'KDoc'}


def get_indent(line):
    indent = 0
    while line[indent] == ' ':
        indent += 1
    return indent


def get_node_type(node):
    node = node.strip()
    if all(i in CAP_OR_UNDER for i in node):
        return node
    elif node.startswith('PsiWhiteSpace'):
        return 'WHITE_SPACE'
    elif node.startswith('PsiElement'):
        return node[node.find('(') + 1:node.find(')')]
    elif node.startswith('PsiComment'):
        return 'BLOCK_COMMENT'
    elif node.startswith('PsiErrorElement'):
        return 'ERROR_ELEMENT'
    elif any(node.startswith(i) for i in SKIP_PREFIXES):
        return None
    return node


def to_old_style(tree_node):
    if isinstance(tree_node, list):
        i = 0
        n = len(tree_node)
        while i < n and tree_node[i]['type'] not in ('KDoc', 'LAMBDA_EXPRESSION'):
            i += 1
        if i < n:
            if 'children' in tree_node[i - 1]:
                del tree_node[i - 1]['children']
            tree_node = tree_node[:i]
        return [to_old_style(i) for i in tree_node]
    elif isinstance(tree_node, dict):
        return {k: to_old_style(v) for k, v in tree_node.items()}
    if tree_node in ('EOL_COMMENT', 'SHEBANG_COMMENT'):
        return 'BLOCK_COMMENT'
    return tree_node


def psi_to_json(psi_path):
    indents = []
    parents = []
    prev_node = None
    tree = []
    with open(psi_path, 'rt') as psi_file:
        skip_indent = -1
        for line in psi_file:
            if prev_node is None:
                cur_node = {'type': 'FILE'}
                tree.append(cur_node)
                prev_node = cur_node
                indents.append(0)
                continue

            indent = get_indent(line)

            if skip_indent != -1:
                if indent > skip_indent:
                    continue
                else:
                    skip_indent = -1

            node_type = get_node_type(line)
            if node_type is None:
                skip_indent = indent
                continue
            cur_node = {'type': node_type}
            if indent > indents[-1]:
                prev_node['children'] = []
                parents.append(prev_node)
                indents.append(indent)
            else:
                while indents[-1] != indent:
                    indents.pop()
                    parents.pop()
            parents[-1]['children'].append(cur_node)
            prev_node = cur_node
            # if node_type in TYPES_NOT_TO_EXPAND:
                # skip_indent = indent
    return tree
