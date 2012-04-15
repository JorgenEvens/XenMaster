#!/bin/sh

echo 'Running XenMaster post-install script...';
echo 'Making xen boot by default';

sed -i 's/GRUB_DEFAULT=.\+/GRUB_DEFAULT="Xen 4.1-amd64"/' /etc/default/grub;
update-grub;

echo 'Setting up networking...';

cat > /etc/network/interfaces <<EOF 
# This file describes the network interfaces available on your system
# and how to activate them. For more information, see interfaces(5).

# The loopback network interface
auto lo
iface lo inet loopback

EOF

echo 'Setting up the xensource-inventory file';

control_domain=`uuidgen`;
installation=`uuidgen`;

cat > /etc/xensource-inventory << EOF
CURRENT_INTERFACES='xenbr0'
BUILD_NUMBER='0'
CONTROL_DOMAIN_UUID='${control_domain}'
INSTALLATION_UUID='${installation}'
MANAGEMENT_INTERFACE='xenbr0'
PRIMARY_DISK='/dev/sda1'
EOF

echo 'Setting xapi as the toolstack';
cat > /etc/default/xen << EOF
TOOLSTACK=xapi
EOF

echo 'Make sure xend does *not* start';
sed -i -e 's/xend_start$/#xend_start/' -e 's/xend_stop$/#xend_stop/' /etc/init.d/xend;
update-rc.d xendomains disable;

echo 'Make SSL comms start on boot';

update-rc.d xapissl defaults;

echo 'Installing XAPI plugins';

wget http://#{bootstrap-server-address}/setup/plugins.tar.gz -O /root/xapi-plugins.tar.gz;
tar -C /usr/lib/xcp/plugins -xvzf /root/xapi-plugins.tar.gz;

echo 'Saying hi to bootstrap server';

mv /etc/motd.tail /etc/motd.tail.default;
wget http://#{bootstrap-server-address}/setup/motd -O /etc/motd.tail

echo 'Linking keymap files';
mkdir -p /usr/share/qemu/
ln -s /usr/share/qemu-linaro/keymaps /usr/share/qemu/keymaps

cat > /root/setup-done.sh << EOF
#!/bin/sh
echo 'Removing message...';
mv /etc/motd.tail.default /etc/motd.tail;
echo 'Done! Enjoy your coffee';
EOF

cat > /etc/xen/scripts/qemu-ifup << EOF
#!/bin/sh

echo 'config qemu network with xen bridge for '
echo \$*

ifconfig \$1 0.0.0.0 up
ovs-vsctl add-port \$2 \$1
EOF

echo 'Post Install finished';