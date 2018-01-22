<div id="post">
<?php
	foreach( $_POST as $key=>$profit )
		echo "<b id='$key'>$profit</b>";
?>
</div>
<div id="get">
<?php
	foreach( $_GET as $key=>$profit )
		echo "<b id='$key'>$profit</b>";
?>
</div>
