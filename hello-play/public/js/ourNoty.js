	
	
		function generateSuccessNoty() {
		
         var n = noty({
             type        : 'success',
             text        : 'success',
             dismissqueue: true,
             layout      : 'bottomRight',
             theme       : 'defaulttheme',
             maxvisible  : 10
			 
         });
	
			
		}
		
		
		function generateErrorNoty() {
        var n = noty({
            text        : 'error',
            type        : 'error',
            dismissQueue: true,
            layout      : 'bottomRight',
            theme       : 'defaultTheme',
            maxVisible  : 10,
			animation: {
				open: {height: 'toggle'},
				close: {height: 'toggle'},
				easing: 'swing',
				speed: 500 // opening & closing animation speed
			},
			timeout: true
        });
			
		}
