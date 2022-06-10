import React, { useState, useEffect } from "react";
import UserService from "../services/user.service";
import AuthService from "../services/auth.service";
import { Navigate, useNavigate, Link } from "react-router-dom";
import EventBus from "../common/EventBus";
import PacmanLoader from "react-spinners/PacmanLoader";
import { Button } from "@mui/material";


const AdminView = () => {

  let navigate = useNavigate();
  const [userList, setUserList] = useState([]);
  const loggedIn = AuthService.getCurrentUser()?.roles.includes("ADMIN");

  const handleDeleteAccount = (firstName, lastName, userId) => {
    const consent = window.prompt('Jeśli chcesz usunąć konto i wszystkie dane z nim związane wpisz imię i nazwisko użytkownika.\nTen proces jest nie odwracalny!!!');
    if (consent === `${firstName} ${lastName}`) {
      UserService.adminDeleteUserAccount(userId)
      .then((response) => {
        if (response.status === 200) {
          // window.alert("Usunięto użytkownika" + firstName + " " + lastName)
          // const tmp = userList;
          // const newUserList = tmp.filter((user,index) => {
          //   if(index > 0) {
          //     return (user.id !== userId)
          //   }
          // })
          // handleUserList(newUserList);
          navigate(0);
        }
      },(error) => {
        const _userList =
          (error.response &&
            error.response.data &&
            error.response.data.message) ||
            error.message ||
            error.toString();
            setUserList(_userList);
          }
        );
    }
  }

  useEffect(() => {

    (async () => {
      await UserService.getAdminView()
      .then((response) => {
        handleUserList(response.data);
      },
      (error) => {
        const _userList =
          (error.response &&
            error.response.data &&
            error.response.data.message) ||
          error.message ||
          error.toString();
        setUserList(_userList);

        if (error.response && error.response.status === 401) {
            EventBus.dispatch("logout");
          }
      }
    );
    })()  

  }, []);

  const handleUserList = (data) => {
    const usersData = Array.from(data)
    const usersList = usersData.map((user, index) => {
    if( index > 0 )
      return (
        <li key={index} className="user-list text-center">
          <Link to={`/admincallendar/${user.id}`} user={user} className="link-li"> 
            <p>{user.firstName}</p>
            <p>{user.lastName}</p>
          </Link>
          <Button className="user-list-button m-1" size="small" variant="contained" color="error" onClick={() => handleDeleteAccount(user.firstName, user.lastName, user.id)}>Usuń użytkownika</Button>
        </li>
      )
    })
    setUserList(usersList);
  }

  if  (!userList) 
  return (
    <div className="w-100 h-100 d-flex align-center align-items-center justify-content-center">
    <PacmanLoader color="#3688D7" />
    </div>
  );
  
  return (
    <>
    {loggedIn ?  
        <div className="container">
        <header className="jumbotron">
          <h1>Lista użytkowników</h1>
          <ul className="list-unstyled">{userList}</ul>
        </header>
        </div>
  : (<Navigate to="/login" />)
  }
  </>
  );
};  

export default AdminView;