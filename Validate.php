<?
  header('content-type: text/html; charset=utf-8'); 
  // 데이터베이스 접속 문자열. (db위치, 유저 이름, 비밀번호)
  $connect=mysqli_connect( "localhost", "hong123", "qrad9768!") or  
        die( "SQL server에 연결할 수 없습니다.");
 
  mysqli_query($connect, "SET NAMES UTF8");
  // 데이터베이스 선택
  mysqli_select_db($connect, "hong123");
 
  // 세션 시작
  session_start();
 
  $id = $_POST['u_id'];
  $sql = "SELECT userPassword FROM USER WHERE userID = '$id'";
 
  $result = mysqli_query($connect, $sql);
 
  // result of sql query
  if($result)
  {
    $row = mysqli_fetch_array($result);
    if(is_null($row['userPassword']))
    {
      echo "available";
    }
    else
    {
      echo "unavailable";
    }
  }
  else
  {
   echo mysqli_errno($connect);
  }
?>