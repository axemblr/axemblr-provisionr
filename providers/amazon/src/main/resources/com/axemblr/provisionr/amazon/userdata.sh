#!/bin/bash
set -e -x
export DEBIAN_FRONTEND=noninteractive

# adds the distribution codename as a shell var
. /etc/lsb-release

# add the puppet package repository
wget http://apt.puppetlabs.com/puppetlabs-release-$DISTRIB_CODENAME.deb
dpkg -i puppetlabs-release-$DISTRIB_CODENAME.deb

apt-get update
apt-get install puppet-common

# TODO implement a special action for global package upgrades 
# apt-get upgrade -y