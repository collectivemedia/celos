#!/usr/bin/env ruby
require "aws-sdk"

access_key_id = ENV["AWS_ACCESS_KEY_ID"]
secret_access_key = ENV["AWS_SECRET_ACCESS_KEY"]
celos_sec_gp = ENV["CELOS_GROUP_UID"]

if access_key_id.nil? || secret_access_key.nil?
  abort("You must set $AWS_ACCESS_KEY_ID and $AWS_SECRET_ACCESS_KEY in environment variables.")
end


AWS.config(
  :access_key_id => ENV["AWS_ACCESS_KEY_ID"],
  :secret_access_key => ENV["AWS_SECRET_ACCESS_KEY"]
)

def ec2 
  @ec2 ||= AWS::EC2.new
end

def group_create(gid,celos_sec_gp)
  sec_group = ec2.security_groups.create("#{celos_sec_gp}-#{gid}")
  sec_group.authorize_ingress(:tcp, 1..65535, '0.0.0.0/0'  )	
  sec_group.authorize_ingress(:udp, 1..65535, '0.0.0.0/0'  )
  sec_group.allow_ping

end

group_create('nn',celos_sec_gp)
group_create('dn',celos_sec_gp)
group_create('misc',celos_sec_gp)
