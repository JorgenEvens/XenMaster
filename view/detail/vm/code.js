(function( $, app ){
	
	var show = this.show,
		elem = {
			name: {
				property: 'nameLabel'
			},
			
			os: {
				property: ''
			},
			ram: {
				property: 'maximumStaticMemory'
			},
			cpu: {
				property: 'maxVCPUs'
			},
			boot: {
				property: ''
			},
			
			nic: {
				property: ''
			},
			vnc: {
				property: ''
			},
			
			pae: {},
			usb: {},
			apic: {}
		},
		parseValue = function( name, value ) {
			if( name == 'ram' ) {
				value = parseBytes( value );
			}
			
			if( value == null ) {
				return 'N/A';
			}
			
			return value;
		},
		parseBytes = function( bytes ) {
			var size = 0,
				sizeTypes = ['B', 'KB', 'MB', 'GB', 'TB' ];
			
			while( bytes >= 1024 ) {
				bytes = bytes/1024;
				size++;
			}
			
			return bytes + sizeTypes[size];
		};
	
	/*
	 * Map editable elements into elem.
	 */
	this.setup = function( vm_detail ) {
		console.log( vm_detail );
		
		var d = $(this.dom),
			e = elem,
			i = null,
			item = null,
			value = null;
		
		for( i in e ) {
			item = e[i];
			
			if( !item.element ) {
				item.element = d.find( '.vm_' + i );
			}
			
			value = parseValue( i, vm_detail[ item.property ] );
			
			
			item.element.html( value );
		}
	};
	
	this.show = function( vm_detail ) {
		show.call(this);
		this.setup( vm_detail );
	};
});