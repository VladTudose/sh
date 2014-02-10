	
	

$(document).ready(function() {

		$("#formButton" ).click(function() {
			
			event.preventDefault();
			generateSuccessNoty();
			setTimeout(function () {
					$("form" ).submit();
			}, 2000);
			
		});	
		
		
	
	
});