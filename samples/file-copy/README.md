# Celos Sample Workflow

An Oozie workflow that simply copies (moves, actually) its hourly
input files to an output directory.

The [input](input) dir contains hourly text files.

The [workflow](workflow) dir contains the Oozie [`workflow.xml`](workflow/workflow.xml).

The [`file-copy.json`](file-copy.json) file is the Celos workflow definition.

The [`test.yaml`](test.yaml) file is the Ansible based testing script.
