import React, { useState, useEffect } from "react";
import UserService from "../services/user.service";
import AuthService from "../services/auth.service";
import { Navigate, useParams } from "react-router-dom";
import BigCalendar from "./Calendar";
import EventBus from "../common/EventBus";
import PacmanLoader from "react-spinners/PacmanLoader";


const AdminCallendar = () => {

  const { userId } = useParams();
  const [userName, setUserName] = useState("");
  const [events, setEvents] = useState(null);

  const loggedIn = AuthService.getCurrentUser()?.roles.includes("ADMIN");

  useEffect(() => {

    (async () => {

      await UserService.getUser(userId)
      .then((response) => {
        // console.log(response.data)
        const updateEvents = []
        response.data.postList.forEach( post => {
          updateEvents.push(
            {
            id: post.id,
            title: post.title,
            allDay: true,
            trainingDay: post.trainingDay,
            trainingDone: post.trainingDone,
            commentList: post.commentList
          }
          )
        })

        setEvents(updateEvents);
        setUserName(`${response.data.firstName} ${response.data.lastName}`)
      },
      (error) => {
        const _content =
          (error.response &&
            error.response.data &&
            error.response.data.message) ||
          error.message ||
          error.toString();
        setEvents(_content);

        if (error.response && error.response.status === 401) {
            EventBus.dispatch("logout");
          }
      }
    );
    })()  

  }, [userId]);

  if  (!events) 
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
          <h1>Kalendarz użytkownika {userName}</h1>
          <div className="calendar">
            <BigCalendar events={events} userId={userId} setEvents={setEvents}/>
          </div>
        </header>
        </div>
  : (<Navigate to="/login" />)
  }
  </>
  );
};  

export default AdminCallendar;