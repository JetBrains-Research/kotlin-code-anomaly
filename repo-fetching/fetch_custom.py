import os
import subprocess

out_dir = '../repos/'
repos = [
    "git@github.com:Raizlabs/DBFlow.git",
    "https://github.com/dbacinski/Design-Patterns-In-Kotlin.git",
    "git@github.com:kittinunf/Fuel.git",
    "git@github.com:SalomonBrys/Kodein.git",
    "https://github.com/vicboma1/Kotlin-Koans.git",
    "https://github.com/dodyg/Kotlin101.git",
    "git@github.com:SalomonBrys/Kotson.git",
    "git@github.com:ReactiveX/RxKotlin.git",
    "git@github.com:http4k/http4k.git",
    "git@github.com:Kotlin/kotlin-coroutines.git",
    "https://github.com/JetBrains/kotlin-examples.git",
    "https://github.com/Kotlin/kotlin-koans.git",
    "git@github.com:JetBrains/kotlin.git",
    "git@github.com:Kotlin/kotlinx.coroutines.git",
    "git@github.com:kohesive/kovert.git",
    "git@github.com:ktorio/ktor.git",
    "git@github.com:nhaarman/mockito-kotlin.git",
    "git@github.com:perwendel/spark-kotlin.git",
    "git@github.com:wasabifx/wasabi.git",

    "git@github.com:arturbosch/detekt.git",
    "git@github.com:edvin/tornadofx.git",
    "git@github.com:jankotek/mapdb.git",
    "git@github.com:requery/requery.git",
    "git@github.com:square/sqldelight.git",
    "git@github.com:JetBrains/Exposed.git",
]


def git(*args):
    return subprocess.check_call(['git'] + list(args))


os.chdir(out_dir)
subprocess.run('ls')

for repo in repos:
    git("clone", repo)
