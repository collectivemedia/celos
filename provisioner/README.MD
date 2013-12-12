#BOOTSTRAP CLUTSTER
cd <Celos_dir>/provisioner
./bootstrap.sh

#DESTROY CLUSTER
./destroy.sh

#ENVIRONMENTAL VARIABLES TO BE SET
export EC2_KEYPAIR=""
export CELOS_GROUP_UID=""
#collective aws account
export AWS_ACCESS_KEY_ID=""
export AWS_SECRET_ACCESS_KEY=""
export ANSIBLE_HOST_KEY_CHECKING=False
