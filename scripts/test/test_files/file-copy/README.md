# Celos Sample Workflow

An Oozie workflow that simply copies (moves, actually) its hourly
input files to an output directory.

The [input](input) dir contains hourly text files.

The [output](output) dir contains the expected outputs (the same
files, in this case, since the workflow is basically an identity
function).

The [workflow](workflow) dir contains the Oozie [`workflow.xml`](workflow/workflow.xml).

The [`file-copy.json`](file-copy.json) file is the Celos workflow definition.

The [`test.yaml`](test.yaml) file is the Ansible based testing script.
