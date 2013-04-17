<?php
session_start();
$a = $_SESSION['usr'];
if($_SESSION['loggedIn'] != "true"){
header("location:main_login.php");
}
?>

<html>
<body>
Login Successful
<applet height=700 width=800 code="ClientJApplet.class" archive="Client.jar">
<param name="username" value="<?php echo $a; ?>">
<param name="host" value="localhost">
<param name="port" value="8087">
</applet>

</body>
</html>
