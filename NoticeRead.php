<?
    header('content-type: application/json; charset=utf-8'); 
    // 데이터베이스 접속 문자열. (db위치, 유저 이름, 비밀번호)
    $connect=mysqli_connect( "localhost", "hong123", "qrad9768!") or  
        die( "SQL server에 연결할 수 없습니다.");
    
    mysqli_query($connect, "SET NAMES UTF8");
   // 데이터베이스 선택
   mysqli_select_db($connect, "hong123");
 
   // 세션 시작
   session_start();
 
   $nbTime = $_GET['nb_time'];
   
   $sql = "SELECT userID, userName, nbTitle, nbContent FROM notice WHERE nbTime = '$nbTime'";
 
   $result = mysqli_query($connect, $sql);
   $data = array();

   if(!$result)
            die("mysql query error");
   else
   {
       while($row = mysqli_fetch_array($result)) {
           array_push($data, array('userID' => $row[0], 'userName' => $row[1], 'nbTitle' => $row[2], 'nbContent' => $row[3]));
       }
        
       $json = json_encode(array("webnautes"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
       echo $json;
   }
        
 
?>