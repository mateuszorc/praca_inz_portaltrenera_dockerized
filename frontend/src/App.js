import React, { useState, useEffect } from "react";
import { Routes, Route, Link } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import "./App.css";
import AuthService from "./services/auth.service";
import Login from "./components/Login";
import Registration from "./components/Registration";
import Profile from "./components/Profile";
import UserView from "./components/UserView";
import AdminView from "./components/AdminView";
import EventBus from "./common/EventBus";
import AdminCallendar from "./components/AdminCallendar";
import PrivacyPolicy from "./components/minor/PrivacyPolicy";


function App() {

  const [showAdminView, setShowAdminView] = useState(false);
  const [currentUser, setCurrentUser] = useState(undefined);
  
  useEffect(() => {
    const user = AuthService.getCurrentUser();
    if(user) {
      setCurrentUser(user);
      setShowAdminView(user.roles.includes("ADMIN"));
    }

    EventBus.on("logout", () => {
      logOut();
    })
    return () => {
      EventBus.remove("logout");
    }

  },[])

  const logOut = () => {
    AuthService.logout();
    setShowAdminView(false);
    setCurrentUser(undefined);
  }

  const clasName = () => {
    const classs = currentUser ? "sidebar bg-dark" : null;
    return classs
  }

  return (
    <div className={currentUser ? "main-container" : ""}>

      {currentUser && 
      <header className="header">
        <div className="avatar">
          <img src="/avatar1.png" alt="avatar"/>
        </div>
        <div className="info">
          {currentUser ? 
          <div>
            {showAdminView ? 
            <span><h1><strong>Trenerze</strong>,</h1><h4> Twoi zawodnicy dają z siebie wszystko!</h4></span>
            : 
            <span><h1><strong>{currentUser.firstName}</strong>,</h1><h4> świetnie, że dbasz o siebie!</h4></span>}
          </div> : null}
        </div>
      </header>
      }
      
      <div className={currentUser ? "content-container" : ""}>
        {currentUser &&
        <nav className={clasName()}>
          <ul className="list-unstyled mb-0 pb-0 d-flex flex-column h-100">
            {showAdminView && (
              <>
              <li className="nav-item">
                <Link to={"/admin"} className="nav-link">
                  Lista użytkowników
                </Link>
              </li>
              <li className="nav-item">
                <Link to={"/registration"} className="nav-link">
                  Rejestracja użytkownika
                </Link>
              </li>
              <li className="nav-item">
                <Link to={`/users/${currentUser.id}/profile`} className="nav-link">
                  Twoje konto
                </Link>
              </li>
              <li className="nav-item mt-auto border-top">
                <a href="/login" className="nav-link" onClick={logOut}>
                  Logout
                </a>
              </li>
              </>)
            }

            {currentUser && !showAdminView && (
              <>
              <li className="nav-item">
                <Link to={`/users/${currentUser.id}`} className="nav-link">
                  Kalendarz
                </Link>
              </li>
              <li className="nav-item">
                <Link to={`/users/${currentUser.id}/profile`} className="nav-link">
                  Twoje konto
                </Link>
              </li>
              <li className="nav-item mt-auto border-top">
                <a href="/login" className="nav-link" onClick={logOut}>
                  Logout
                </a>
              </li>
              </>
            )}
          </ul>
        </nav>
        }   
        <div className="mainContent">
          <Routes>
            <Route exact path="/" element={<Login/>} />
            <Route exact path="/login" element={<Login/>} />
            <Route exact path="/registration" element={<Registration/>} />
            <Route exact path="/users/:userId/profile" element={<Profile/>} />
            <Route exact path="/users/:userId" element={<UserView/>} />
            <Route exact path="/admincallendar/:userId" element={<AdminCallendar />}/>
            <Route exact path="/privacy-policy" element={<PrivacyPolicy />} />
            <Route exact path="/admin" element={<AdminView/>} />
          </Routes>
        </div>
      </div>
    </div>
  );
}

export default App;
