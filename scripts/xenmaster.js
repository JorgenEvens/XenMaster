DEBUG = document.location.toString().indexOf('debug') > -1;

(function( undefined ){
	
	var init_app = function() {
		/*
		 * Initialize application
		 ********************************************************************************************
		 */
		M.load( 'tpl://global/ui', 'tpl://dashboard', 'js://ui/template', 'js://tools/notifier', 'js://ui/notifier',
				function( ui, dashboard, Template, Notifier, Notif ) {
			
			Notifier.publish( 'XenMaster', 'Connection to backend established!' );
			
			var global_ui = new Template({ resource: ui }),
				dashboard_ui = new Template({ resource: dashboard });
			
			global_ui.show();
			dashboard_ui.show();
			
			M.load('tpl://global/eventviewer', function(viewer) {
				var viewerTemplate = new Template({resource: viewer});
				viewerTemplate.show('eventviewer');
			});
			
			if( DEBUG ) {
				M.load( 'tpl://debug/console', 'js://ui/template', function( dc, Template ) {
					var tpl = new Template({resource: dc});
					tpl.show('debug');
				});
			}
			
		});
	},
	
	loaded = false,
	
	// Instances of this application.
	MASTER = null,
	M = null;
	
	var onload = window.onload;
	window.onload = (function(){
		if( onload ) {
			onload();
		}
		
		MASTER = new Application({
			'proxy': 'resources.php',
			'base': '/code',
			'buffer_timeout': 100
		});
		
		M = MASTER;
		
		/*
		 * Setup a connection with backend
		 */
		M.load( 'js://net/xmconnection', 'js://tools/notifier', function( XmCon, Notifier ){
			
			var xm = new XmCon(document.location.host + '/wwscp');
			xm.open();
			
			xm.onopen = function(){
				if( !loaded ) {
					loaded = true;
					init_app();
				}
			};
			
			xm.onerror = function() {
				Notifier.publish( 'XenMaster', 'Unable to connect to XenMaster api. Retry in 5s' );
				window.setTimeout(function(){ xm.open(); }, 5000 );
			};
		});
		
	});
	
}());