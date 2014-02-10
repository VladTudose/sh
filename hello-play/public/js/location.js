	
	

$(document).ready(function() {

		$("#form" ).click(function() {
			
			event.preventDefault();
			generateSuccessNoty();
			setTimeout(function () {
					$("#form" ).submit();
			}, 2000);
			
		});	
		
		
	
	
});