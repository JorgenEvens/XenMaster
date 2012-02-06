/*
 * vm.js
 * Copyright (C) 2011,2012 Jorgen Evens
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * vm.js
 * Copyright (C) 2011,2012 Jorgen Evens
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

(function( ready, app ){
	app.load( 'js://api/entity', 'js://api/vbd', 'js://api/vif',
			function( Entity, VBD, VIF ){
		
		var VM = new Entity( 'VM' );
		
		VM.createStaticMethod( 'build', VM );
		
		VM.createStaticMethod( 'getAll', VM, true );
		
		VM.createMethod( 'create' );
		
		VM.createMethod( 'destroy' );
		
		VM.createMethod( 'start' );
		
		VM.createMethod( 'pause' );
		
		VM.createMethod( 'resume' );
		
		VM.createMethod( 'stop' );
		
		VM.createMethod( 'reboot' );
		
		VM.createMethod( 'suspend' );
		
		VM.createMethod( 'wake' );
		
		VM.createMethod( 'getVBDs', VBD, true );
		
		VM.createMethod( 'getVIFs', VIF, true );

		VM.createMethod( 'setPlatform' );
		
		VM.createMethod( 'getPlatform' );
		
		VM.createMethod( 'setName' );
		
		VM.createMethod( 'setDescription' );
		
		VM.createMethod( 'setMaximumDynamicMemory' );
		
		VM.createMethod( 'setVCPUs' );
		
		ready( VM );
	});
});