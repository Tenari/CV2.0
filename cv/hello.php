<html>
 <head>
  <title>PHP Test</title>
 </head>
 <body>
 <p>Hi <?php echo htmlspecialchars($_POST['name']); ?>.
You are <?php echo (int)$_POST['age']; ?> years old.</p>
 </body>
</html>