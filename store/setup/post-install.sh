#!/bin/sh

echo 'Making xen boot by default';

mv /etc/grub.d/20_linux_xen /etc/grub.d/09_linux_xen;
update-grub;

echo 'Setting up network interfaces';

cat > /etc/network/interfaces <<EOF 
# This file describes the network interfaces available on your system
# and how to activate them. For more information, see interfaces(5).

# The loopback network interface
auto lo xenbr0
iface lo inet loopback

# The primary network interface
iface eth0 inet manual

iface xenbr0 inet dhcp
        bridge_ports eth0

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

echo 'Setting xl as the toolstack';
cat > /etc/default/xen << EOF
TOOLSTACK=xl
EOF

echo 'Make sure xend does *not* start';
sed -i -e 's/xend_start\n/#xend_start\n/' /etc/init.d/xend;
sed -i -e 's/xend_stop\n/#xend_stop\n/' /etc/init.d/xend;

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

echo 'Post Install finished';