<html>
<body>

<?php
	$host="localhost"; // Host name
	$username="root"; // Mysql username
	$password=""; // Mysql password
	$db_name="game"; // Database name
	
	$mapLastRow = 11;
	$mapLastCol = 21;
	
	$terrain = array(	array(1,1,1, 1,1,1,1,1, 1,1,1,1,1,1,1,1,1,1,1,1,1,1),
						array(1,3,3, 3,1,3,1,3, 3,1,4,4,4,1,4,4,1,4,4,4,4,1),
						array(1,3,3, 3,1,3,1,3, 3,1,4,4,4,1,4,4,1,4,4,4,4,1),
						array(1,3,3, 3,1,3,1,3, 3,1,4,4,4,1,4,4,1,4,4,4,4,1),
						array(1,1,10,1,1,3,1,11,1,1,4,4,4,1,1,1,1,4,4,4,4,1),
						array(1,3,3, 3,3,3,3,3, 3,3,3,3,3,3,3,3,3,3,4,4,4,1),
						array(1,4,4, 4,4,4,4,4, 4,4,4,4,4,4,4,4,4,4,4,4,4,1),
						array(1,4,4, 4,4,4,4,4, 4,4,4,4,4,4,4,4,4,4,4,4,4,1),
						array(1,4,4, 4,4,4,4,4, 4,4,4,4,4,4,4,4,4,4,4,4,4,1),
						array(1,4,1, 4,4,4,4,4, 4,4,4,4,4,4,4,4,4,4,4,4,4,1),
						array(1,4,4, 4,4,4,4,4, 4,4,4,4,4,4,4,4,4,4,4,4,4,1),
						array(1,1,1, 1,1,1,1,1, 1,1,1,1,1,1,1,1,1,1,1,1,1,1)
					);

	// Connect to server and select databse.
	$con = mysql_connect("$host", "$username", "$password")or die("cannot connect");
	mysql_select_db("$db_name")or die("cannot select DB");
	
	
	for($i=0;$i<$mapLastRow;$i+=1){
		for($j=0;$j<$mapLastCol;$j+=1){
			
			mysql_query("INSERT INTO ".$tableName." VALUES (".$j.", ".$i.", ".$terrain[$i][$j]")", $con);
			
		}
	}
	

?>

</body>
</html>