<?php 

include_once dirname(__FILE__) . '/DbConnect.php';

$response = array();


$db = new DbConnect();
$con = $db->connect();
$stmt = $con->prepare("SELECT * FROM Names");
$stmt->execute();
$stmt->bind_result($id, $Name);

$names = array();

while($stmt->fetch())
{
	$name = array();
	$name['id'] = $id;
	$name['Name'] = $Name;

	array_push($names, $name);
}

$response['Names'] = $names;


echo json_encode($response);


 ?>