import React, { useState, useEffect } from "react";
import UserService from "../services/user.service";
import AuthService from "../services/auth.service";
import { Navigate, useParams } from "react-router-dom";
import EventBus from "../common/EventBus";
import BigCalendar from "./Calendar";
import PacmanLoader from "react-spinners/PacmanLoader";

const UserView = () => {

  // const [content, setContent] = useState("");
  const { userId } = useParams();
  const [events, setEvents] = useState(null);
  const loggedIn = AuthService?.getCurrentUser();

  useEffect(() => {

    (async () => {
      await UserService.getUserView()
      .then((response) => {
        // console.log(response.data)

        const updateEvents = []
        response.data.forEach( post => {
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
      }, 
      (error) => {
        const _events =
          (error.response &&
            error.response.data &&
            error.response.data.message) ||
          error.message ||
          error.toString();
          
          if (error.response && error.response.status === 401) {
            EventBus.dispatch("logout");
          }
        setEvents(_events);
      } 
      );
    })()
  },[]);


  if  (!events && loggedIn) 
  return (
    <div className="w-100 h-100 d-flex align-center align-items-center justify-content-center">
    <PacmanLoader color="#3688D7" />
    </div>
  );

  return (
    <>
    { (loggedIn) ? 
    <div className="calendar">
      <BigCalendar events={events} userId={userId} setEvents={setEvents}/>
    </div>
    : <Navigate to="/login" />
    }
    </>
  );
};
export default UserView;
