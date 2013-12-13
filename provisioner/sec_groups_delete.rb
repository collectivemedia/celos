#!/usr/bin/env ruby
require "aws-sdk"

access_key_id = ENV["AWS_ACCESS_KEY_ID"]
secret_access_key = ENV["AWS_SECRET_ACCESS_KEY"]
celos_sec_gp = ENV["EC2_KEYPAIR"]

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

ec2.security_groups.filter('group-name', "#{celos_sec_gp}-nn").each do |group|
#ec2.security_groups.each do |group|
  group.delete
end 

ec2.security_groups.filter('group-name', "#{celos_sec_gp}-dn").each do |group|
#ec2.security_groups.each do |group|
  group.delete
end 

ec2.security_groups.filter('group-name', "#{celos_sec_gp}-misc").each do |group|
#ec2.security_groups.each do |group|
  group.delete
end 

