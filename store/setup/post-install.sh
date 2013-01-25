#!/bin/sh

echo 'Running XenMaster post-install script...';

sed -i 's/GRUB_DEFAULT=.\+/GRUB_DEFAULT="Xen 4.1-amd64"/' /etc/default/grub;
update-grub;
echo 'Made xen boot by default';

cat > /etc/network/interfaces <<EOF 
# This file describes the network interfaces available on your system
# and how to activate them. For more information, see interfaces(5).

# The loopback network interface
auto lo
iface lo inet loopback

EOF

MGMT=xenbr0
echo 'MGMT=xenbr0' > /etc/default/xcp

echo 'Network configured for xcp-networkd'

control_domain=`uuidgen`;
installation=`uuidgen`;

cat > /etc/xensource-inventory << EOF
CURRENT_INTERFACES='${MGMT}'
BUILD_NUMBER='0'
CONTROL_DOMAIN_UUID='${control_domain}'
INSTALLATION_UUID='${installation}'
MANAGEMENT_INTERFACE='${MGMT}'
EOF
echo 'XCP inventory file was installed';

cat > /etc/default/xen << EOF
TOOLSTACK=xapi
EOF
echo 'xapi was set as the toolstack';

sed -i -e 's/xend_start$/#xend_start/' -e 's/xend_stop$/#xend_stop/' /etc/init.d/xend;
update-rc.d xendomains disable;
echo 'Made sure xend does *not* start';

wget http://#{bootstrap-server-address}/setup/plugins.tar.gz -O /root/xapi-plugins.tar.gz;
tar -C /usr/lib/xcp/plugins -xvzf /root/xapi-plugins.tar.gz;
echo 'Installed XAPI plugins'


mv /etc/motd.tail /etc/motd.tail.default;
wget http://#{bootstrap-server-address}/setup/motd -O /etc/motd.tail
echo 'Installed motd';

mkdir -p /usr/share/qemu/
ln -s /usr/share/qemu-linaro/keymaps /usr/share/qemu/keymaps
echo 'Linked keymap files';

cat > /root/setup-done.sh << EOF
#!/bin/sh
echo 'Removing message...';
mv /etc/motd.tail.default /etc/motd.tail;
echo 'Done! Enjoy your coffee';
EOF

cat > /etc/xen/scripts/qemu-ifup << EOF
#!/bin/sh

echo -c 'config qemu network with xen bridge for '
echo \$*

ifconfig \$1 0.0.0.0 up
ovs-vsctl add-port \$2 \$1
EOF
echo 'Configured qemu network for Open vSwitch'

wget http://#{bootstrap-server-address}/setup/xcp -O /etc/init.d/xcp
chmod +x /etc/init.d/xcp
update-rc.d xcp defaults
echo 'Installed stack boot script'

update-rc.d xcp-xapi remove
update-rc.d xcp-fe remove
update-rc.d xcp-v6d remove
update-rc.d xcp-networkd remove
update-rc.d xcp-squeezed remove
update-rc.d openvswitch-switch remove
update-rc.d xen remove
echo 'Disabled defunct LSB scripts'

echo 'Post Install finished';