packer {
  required_plugins {
    amazon = {
      source  = "github.com/hashicorp/amazon"
      version = ">= 1.0.0"
    }
  }
}

variable "aws_region" {
  type    = string
  default = "us-east-1"
}

variable "source_ami" {
  type    = string
  default = "ami-06db4d78cb1d3bbf9" # Debian-12
}

variable "ssh_username" {
  type    = string
  default = "admin"
}

variable "subnet_id" {
  type    = string
  default = "subnet-0ef821d9bbfe01892"
}

# https://www.packer.io/plugins/builders/amazon/ebs
source "amazon-ebs" "my-ami" {
  region          = "${var.aws_region}"
  ami_name        = "csye6225_${formatdate("YYYY_MM_DD_hh_mm_ss", timestamp())}"
  ami_description = "AMI for CSYE 6225"
  profile         = "default"
  #  ami_regions = [
  #    "us-east-1",
  #  ]
  #  owners = []
  ami_users     = ["956043594788"]
  instance_type = "t2.micro"
  source_ami    = "${var.source_ami}"
  ssh_username  = "${var.ssh_username}"
  subnet_id     = "${var.subnet_id}"

  launch_block_device_mappings {
    delete_on_termination = true
    device_name           = "/dev/xvda"
    volume_size           = 25
    volume_type           = "gp2"
  }
}

build {
  sources = ["source.amazon-ebs.my-ami"]

  provisioner "file" {
    source      = "../CloudAssignment03/target/CloudAssignment03-0.0.1-SNAPSHOT.jar"
    destination = "/tmp/CloudAssignment03-0.0.1-SNAPSHOT.jar"
  }

  provisioner "file" {
    source      = "../opt/users.csv"
    destination = "/tmp/users.csv"
  }

  provisioner "shell" {
    environment_vars = [
      "DEBIAN_FRONTEND=noninteractive",
      "CHECKPOINT_DISABLE=1"
    ]
    script = "../setup.sh "
  }

  provisioner "shell" {
    inline = [
      "sudo mv /tmp/users.csv /opt/users.csv",
      "sudo mv /tmp/CloudAssignment03-0.0.1-SNAPSHOT.jar /opt/CloudAssignment03-0.0.1-SNAPSHOT.jar"
    ]
  }
  #  post-processor "ami" {
  #    keep_input_artifact = true
  #    snapshot_users      = ["956043594788"]
  #    ami_name            = "my-ami-shared"
  #  }
}