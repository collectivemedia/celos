## Credentials

Send an email to Sarguru, cc'ing sysadmins@collective.com,
and ask for AWS access to the virtual testing cluster.

You will get:

Username:
sample_your_name

Access Key ID:
SAMPLEKJHDKUAEMACSDAILRULESKDKLAJ

Secret Access Key:
sampleAAHdfDfgLGNUKJAHjIShDUINOTHDWWJUNIXDOIAAA

Password:
sample98313379843$hfs

# Get EC2 Keypair from AWS Console

Sign in to https://collective.signin.aws.amazon.com/console using
username and password from above.

Go to EC2 Console -> Key Pairs -> Import Key Pair and upload your
usual public key, giving it a unique name.

# Environment Variables

You need to set the following variables:

    export EC2_KEYPAIR="keypair-name-from-previous-step"
    export CELOS_GROUP_UID=$EC2_KEYPAIR
    export AWS_ACCESS_KEY_ID="SAMPLEKJHDKUAEMACSDAILRULESKDKLAJ"
    export AWS_SECRET_ACCESS_KEY="sampleAAHdfDfgLGNUKJAHjIShDUINOTHDWWJUNIXDOIAAA"
    export ANSIBLE_HOST_KEY_CHECKING=False

(Add these to your shell RC file.)

# Software Prerequisites

- Ansible: http://ansibleworks.com/docs/intro_installation.html
- Ruby Gems: gem install aws-sdk clap

# Gooooooooooooooo!

    ./bootstrap.sh

(Of course, this will not work the first time.  I mean, we're talking
about computers here, right?  Heck, distributed computers!)

Once you're done, you can shut down the cluster with:

    ./destroy.sh
