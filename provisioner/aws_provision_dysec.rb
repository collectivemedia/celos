#!/usr/bin/env ruby

require "aws-sdk"
require "clap"

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

def create_instance_nn(num_instances = 1,celos_sec_gp)
  options = {
    key_name: ENV["EC2_KEYPAIR"],
    image_id: "ami-bd6d40d4",
    instance_type: "m1.small",
    security_groups: "#{celos_sec_gp}-nn",
    count: num_instances.to_i,
    availability_zone: "us-east-1b"
  }

  instances = Array(ec2.instances.create(options))
  sleep 2 while instances.any? { |i| i.status == :pending }
  #puts instances.inspect
end

def create_instance_dn(num_instances = 1,celos_sec_gp)
  options = {
    key_name: ENV["EC2_KEYPAIR"],
    image_id: "ami-bd6d40d4",
    instance_type: "m1.small",
    security_groups: "#{celos_sec_gp}-dn",
    count: num_instances.to_i,
    availability_zone: "us-east-1b"
  }

  instances = Array(ec2.instances.create(options))
  sleep 2 while instances.any? { |i| i.status == :pending }
  #puts instances.inspect
end


def celos_nn_instances(celos_sec_gp)
  ec2.instances.reject do |instance|
    #puts instance.inspect
    is_not_celos_nn = !instance.security_groups.map(&:name).include?("#{celos_sec_gp}-nn")
    terminated = instance.status == :terminated

    terminated || is_not_celos_nn
  end
end

def celos_dn_instances(celos_sec_gp)
  ec2.instances.reject do |instance|
    #puts instance.inspect
    is_not_celos_dn = !instance.security_groups.map(&:name).include?("#{celos_sec_gp}-dn")
    terminated = instance.status == :terminated

    terminated || is_not_celos_dn
  end
end

celos_nn_instances_list = celos_nn_instances(celos_sec_gp) 
celos_dn_instances_list = celos_dn_instances(celos_sec_gp)

def list_instances(celos_nn_instances_list,celos_dn_instances_list)
  celos_nn_instances_list.each do |instance|
    hash = {
      id: instance.id,
      public_ip: instance.ip_address,
      private_ip: instance.private_ip_address,
      launch_time: instance.launch_time,
      status: instance.status
    }

    puts hash
  end

  celos_dn_instances_list.each do |instance|
    hash = {
      id: instance.id,
      public_ip: instance.ip_address,
      private_ip: instance.private_ip_address,
      launch_time: instance.launch_time,
      status: instance.status
    }

    puts hash
  end

end

def write_hostnames(celos_nn_instances_list,celos_dn_instances_list)

  text = File.read("tmp/hosts_tmp.txt")
  inventory_text = File.read("tmp/inventory_tmp.txt")
  celos_nn_instances_list.each do |instance|
 #   puts instance.inspect
    val   = instance.private_ip_address
    val_pub = instance.dns_name
    text  = text.gsub(/NN/, "#{val}")
    inventory_text = inventory_text.gsub(/NN/,"#{val_pub}")  
    hash = { 
      id: instance.id,
      public_ip: instance.ip_address,
      private_ip: instance.private_ip_address,
      dns_name: instance.dns_name,
      launch_time: instance.launch_time,
      status: instance.status
    }   
    puts hash
  end 

  celos_dn_instances_list.each_with_index do |instance,i|
    num = i + 1
    val = instance.private_ip_address
    val_pub = instance.dns_name
    text = text.gsub(/DN0#{num}/,"#{val}")
    inventory_text = inventory_text.gsub(/DN#{num}/,"#{val_pub}")
    hash = { 
      id: instance.id,
      public_ip: instance.ip_address,
      private_ip: instance.private_ip_address,
      dns_name: instance.dns_name,
      launch_time: instance.launch_time,
      status: instance.status
    }   
    puts hash
  end
  File.open("tmp/hosts", "w") {|file| file.puts text} 
  File.open("tmp/inventory","w") {|file| file.puts inventory_text}

end

def preprov_instances(celos_nn_instances_list,celos_dn_instances_list)
  celos_nn_instances_list.each do |instance|
    puts "copying files to #{instance.ip_address}"
    puts `scp -o StrictHostKeyChecking=no ./tmp/hosts ubuntu@#{instance.ip_address}:~/hosts`  
    puts `scp -o StrictHostKeyChecking=no ./setup_script ubuntu@#{instance.ip_address}:~/setup_script`  
    puts `ssh -o StrictHostKeyChecking=no ubuntu@#{instance.ip_address} "sudo ~/setup_script"`
    hash = {
      id: instance.id,
      public_ip: instance.ip_address,
      private_ip: instance.private_ip_address,
      launch_time: instance.launch_time,
      status: instance.status
    }

    puts hash
  end

  celos_dn_instances_list.each_with_index do |instance,i|
    instance_number = i + 1
    puts "on DN0#{instance_number}"
    puts `scp -o StrictHostKeyChecking=no ./tmp/hosts ubuntu@#{instance.ip_address}:~/hosts`  
    puts `scp -o StrictHostKeyChecking=no ./setup_script ubuntu@#{instance.ip_address}:~/setup_script`  
    puts `ssh -o StrictHostKeyChecking=no ubuntu@#{instance.ip_address} "sudo ~/setup_script dn0#{instance_number}"`
 
    hash = {
      id: instance.id,
      public_ip: instance.ip_address,
      private_ip: instance.private_ip_address,
      launch_time: instance.launch_time,
      status: instance.status
    }

    puts hash
  end

end


def terminate_instances(celos_sec_gp)
  puts celos_sec_gp.inspect
  celos_nn_instances(celos_sec_gp).map(&:terminate)
  celos_dn_instances(celos_sec_gp).map(&:terminate)
end

#terminate_instances(celos_sec_gp)
#list_instances
#create_instance_nn(1,celos_sec_gp)
#create_instance_dn(3,celos_sec_gp)
#list_instances(celos_nn_instances_list,celos_dn_instances_list)
#write_hostnames(celos_nn_instances_list,celos_dn_instances_list)
#preprov_instances(celos_nn_instances_list,celos_dn_instances_list)

runner = -> {
  Clap.run ARGV,
    "--list" => -> { list_instances(celos_nn_instances_list,celos_dn_instances_list) },
    "--create-cluster" => ->  {
create_instance_nn(1,celos_sec_gp)
create_instance_dn(3,celos_sec_gp) },
    "--start-cluster" => -> {
write_hostnames(celos_nn_instances_list,celos_dn_instances_list)
preprov_instances(celos_nn_instances_list,celos_dn_instances_list) },
    "--terminate-cluster" => -> { terminate_instances(celos_sec_gp) }
}

runner.call
