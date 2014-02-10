	
$(document).ready(function() {

		function addToRow(row,value,link){
			var row1 = $('<td onclick="document.location=\'http://localhost:9000/api'+link +'\'"></td>').addClass('bar').text(value);
			row.append(row1);
		}	
		function deleteItem(link){
				var url ="/api"+link;
                var x = new XMLHttpRequest();
                x.open("DELETE", url, true);
                x.send(null);
		}
		function addToDeleteRow(row,item){
			var row1 = $('<td ></td>').addClass('bar');
			row.append(row1);
			var deleteButton = $('<button id=\"'+ item._links.self.href +'\"></button>');
			row1.append(deleteButton);
			$(deleteButton).parent().parent().attr('id',   item._links.self.href);
			$(deleteButton).click(function (event) {
				deleteItem($(this).attr('id'));
				$(this).parent().parent().remove();
			});
		}		
		
		$( document ).ready(function() {
			render();
		});
			
		function render() {
		var table = $('<table id=\"data\"></table>').addClass('foo');
			
			row = $('<tr id=\"header\"></tr>');
			table.append(row);
			/*----TO CHANGE
			addToRow(row,"City");
			addToRow(row,"Country");
			addToRow(row,"Latitude");
			addToRow(row,"Longitude");
			addToRow(row,"");
				*/
					
			$.ajax({
                    type: "GET",
                    url: "http://localhost:9000/api/donations",
                    acccept: "application/json",
                    success: function(response) {
							print(response);
                        }
                    })
                
				
			
			$('#container').append(table);
			
		}
		function print(response){
			for(var i=0;i<response._embedded.locations.length;i++)
			{
				var item = response._embedded.locations[i];
				var link = item._links.self.href;
				row = $('<tr ></tr>');
				$("#data").append(row);
				/* - TO_CHANGE
				addToRow(row,item.city,link);
				addToRow(row,item.country,link);
				addToRow(row,item.latitude,link);
				addToRow(row,item.longitude,link);
				addToDeleteRow(row,item);
				*/
			}
		}
	
	
});