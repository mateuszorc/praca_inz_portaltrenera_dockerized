import React, { useState } from 'react';
import { Calendar, momentLocalizer} from 'react-big-calendar';
import withDragAndDrop from 'react-big-calendar/lib/addons/dragAndDrop';
import moment from 'moment';
import "react-big-calendar/lib/css/react-big-calendar.css";
import "react-big-calendar/lib/addons/dragAndDrop/styles.css";
import UserService from '../services/user.service';
import AuthService from '../services/auth.service';
import add from 'date-fns/add';
//---------------------------------------------------------
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import { Box } from '@mui/system';
//---------------------------------------------------------
const localizer = momentLocalizer(moment)
const DragAndDropCallendar = withDragAndDrop(Calendar)

let BigCalendar = ({ events, userId, setEvents }) => {

  const loggedIn = AuthService?.getCurrentUser().roles.includes('ADMIN')
    const [open, setOpen] = useState(false);
    const [openShow, setOpenShow] = useState(false);
    const [title, setTitle] = useState("");
    const [prevTitle, setPrevTitle] = useState("");
    const [comment, setComment] = useState("");
    const [prevComment, setPrevComment] = useState("");
    const [userComment, setUserComment] = useState("");
    const [prevUserComment, setPrevUserComment] = useState("");
    const [start, setStart] = useState(null);
    const [editUserComment, setEditUserComment] = useState(false);
    const [editPost, setEditPost] = useState(false);
    const [selectedEvent, setSelectedEvent] = useState(null);
    const [draggedEvent, setDraggedEvent] = useState(null);

  const handleSelectNewEvent = ({start}) => {
  setOpen(true)
  setStart(start)
  }

  const handleSelectEvent = (event) => {
  setOpenShow(true)
  setSelectedEvent(event)
  }

  const handleChangeTitle = (e) => {
    const title = e.target.value;
    setTitle(title);
  }

  const handleChangeComment = (e) => {
    const comment = e.target.value;
    setComment(comment);
  }

  const handleChangeUserComment = (e) => {
    const comment = e.target.value
    setUserComment(comment);
  }

  const openEditPost = () => {
    setEditPost(true);
    setPrevTitle(selectedEvent.title);
    setTitle(selectedEvent.title);
    setPrevComment(selectedEvent.commentList[0].content);
    setComment(selectedEvent.commentList[0].content);
  }

  const openEditUserComment = () => {
    setEditUserComment(true);
    setPrevUserComment(selectedEvent.commentList[1].content);
    setUserComment(selectedEvent.commentList[1].content);
  }

  const handleClose = () => {
    setOpen(false);
    setTitle("");
    setComment("");
    setStart(null);
    //-----------
    setOpenShow(false);
    setUserComment("");
    setSelectedEvent(null);
    //-----------
    setEditUserComment(false);
    setEditPost(false);
    setPrevTitle("");
    setPrevComment("");
  };

//---------------------------------------------

  const handleAddEvent = () => {
    // console.log("jestem w handleAddEvent")

    if (loggedIn) {

      const startDate = add(new Date(start), {days: 1})
      // console.log(startDate)
      // console.log(title)
      if (title !== undefined && title !== "") {

        if (comment === undefined || comment === "") {
          window.alert("Opis treningu nie może być pusty!")
        } else {
          const newEvent = {
          title: title,
          userId: userId,
          allDay:true,
          trainingDay: startDate.toISOString().slice(0,10),
          trainingDone: false,
          commentList: [],
          // -----------------------------------
          content:comment
        }
        UserService.postAdminPost(newEvent)
        .then(res => {
          setEvents([...events, res.data])
        })
        setOpen(false)
        setTitle("")
        setComment("")
        }  
      }  
    }
  }  

  const handleAddUserComment = () => {
    if (userComment !== undefined && userComment !== "") {
        const newComment = {
          userId: userId,
          postId:selectedEvent.id,
          content:userComment,
          trainingDone: true
        }
        UserService.postUserComment(newComment)
        .then(res => {
          const tmp = events;
          const index = tmp.findIndex(element => element.id === res.data.id)
          tmp[index] = res.data
          setOpenShow(false);
          setUserComment("");
          // handleSetTrainingDone(res.data)
          setSelectedEvent("");
          setEvents(tmp);
        })
      }  
  }

  const handleEditPost = () => {
    if (prevComment !== comment || prevTitle !== title) {
      if (comment !== undefined && title !== undefined) {
      const editedPost = {
          userId: userId,
          postId:selectedEvent.id,
          id: selectedEvent.commentList[0].id,
          title: title,
          trainingDay: selectedEvent.trainingDay,
          content:comment
      } 
      UserService.editPost(editedPost)
        .then(res => {
          const tmp = events;
          const index = tmp.findIndex(element => element.id === res.data.id)
          tmp[index] = res.data
          setOpenShow(false);
          setEditPost(false);
          setPrevComment("");
          setPrevTitle("");
          setTitle("");
          setComment("");
          setSelectedEvent("");
          setEvents(tmp);
        })
      } 
    } else {
      setEditPost(false);
      setPrevComment("");
      setPrevTitle("");
      setTitle("");
      setComment("");
    }
  }

  const handleEditUserComment = () => {
    if (prevUserComment !== userComment) {
      if (userComment !== undefined) {
      const editedComment = {
          userId: userId,
          postId:selectedEvent.id,
          commentId: selectedEvent.commentList[1].id,
          content:userComment
      } 
      UserService.editUserComment(editedComment)
        .then(res => {
          const tmp = events;
          const index = tmp.findIndex(element => element.id === res.data.id)
          tmp[index] = res.data
          setOpenShow(false);
          setEditUserComment(false);
          setUserComment("");
          setSelectedEvent("");
          setEvents(tmp);
        })
    }
    } else {
      setEditUserComment(false);
      setPrevUserComment("");
      setUserComment("");
    }
  }

  const handleDeletePost = () => {
    const consent = window.prompt('Jeśli chcesz usunąć ten post wpisz "tak".\nTen proces jest nie odwracalny!!!');
    if (consent === "tak") {
      UserService.deletePost(selectedEvent.id)
      .then ( res => {
        const tmp = events;
        const newEvents = tmp.filter(post => post.id !== selectedEvent.id);
        setOpenShow(false);
        setEditPost(false);
        setPrevComment("");
        setPrevTitle("");
        setTitle("");
        setComment("");
        setSelectedEvent("");
        setEvents(newEvents);
      })
    }
  }

  const handleDragStart = (event) => {
    setDraggedEvent(event);
  }

  const onDropFromOutside = ({start}) => {
    const startDate = add(new Date(start), {days: 1})
    const trainingDay = startDate.toISOString().slice(0,10)
    if ( trainingDay !== draggedEvent.event.trainingDay ) {
        const event = {
        id: draggedEvent.event.id,
        postId: draggedEvent.event.id,
        title: draggedEvent.event.title,
        userId: userId,
        allDay:true,
        trainingDay: trainingDay,
        trainingDone: false,
        commentList: draggedEvent.event.commentList,
      }
      UserService.editPost(event)
      moveEvent({event, trainingDay});
      setDraggedEvent(null);
    }
    setDraggedEvent(null);
  }

  const moveEvent = ({event, trainingDay}) => {
    const newEvents = events.map(existingEvent => {
      return existingEvent.id === event.id ?
      {...existingEvent, trainingDay}
      :
      existingEvent
    })
    setDraggedEvent(null);
    setEvents(newEvents);
  }

  const eventStyleGetter = (event) => {
    console.log(event);
    if (event.trainingDone) {
      let style = { backgroundColor: "#fff" } 
      return {
        style: { style }
      }
    }
  }
  
    
  return (
    <div>
      <DragAndDropCallendar
      selectable
      localizer={localizer}
      step={60}
      showMultiDayTimes
      events={events}
      startAccessor="trainingDay"
      endAccessor="trainingDay"
      style={{ height: 500 }}
      onSelectSlot={handleSelectNewEvent}  
      onSelectEvent={(event) => handleSelectEvent(event)} 
      onDragStart={handleDragStart}
      onEventDrop={onDropFromOutside}
      eventPropGetter={event => {
        const backgroundColor = event.trainingDone ? "#1b1" : "#"
          return { style: { backgroundColor } }
      }}
    />
    {open && loggedIn &&
      <>
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>Nowy trening</DialogTitle>
        <DialogContent>
          <Box minWidth={500}>
            <TextField
              autoFocus
              margin="normal"
              id="title"
              label="Tytuł treningu"
              type="text"
              fullWidth
              value={title}
              onChange={handleChangeTitle}
              color='info'
              variant="outlined"
            />
            <DialogContentText>
              Opis Treningu:
            </DialogContentText>
            <TextField
              multiline
              autoFocus
              outlined
              margin="normal"
              id="comment"
              label="Opis treningu"
              type="text"
              fullWidth
              value={comment}
              onChange={handleChangeComment}
              variant="outlined"
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Anuluj</Button>
          <Button onClick={handleAddEvent}>Zapisz</Button>
        </DialogActions>
      </Dialog>
    </>
    }
    {openShow && 
      <div className='eventView'>
      <Dialog open={openShow} onClose={handleClose}>
        <DialogTitle>{!editPost && selectedEvent.title}</DialogTitle>
        <Box minWidth={500}>
        <DialogContent>
          {!editPost ? 
            <DialogContentText multiline>
              <TextField
                  multiline
                  margin="normal"
                  id="comment"
                  label="Opis treningu"
                  type="text"
                  fullWidth
                  value={selectedEvent.commentList[0]?.content}
                  variant="outlined"
                />
            </DialogContentText>
          :
            <>
              <TextField
                autoFocus
                margin="normal"
                id="title"
                label="Tytuł treningu"
                type="text"
                fullWidth
                value={title}
                onChange={handleChangeTitle}
                color='info'
                variant="outlined"
              />
              <TextField
                multiline
                autoFocus
                outlined
                margin="normal"
                id="comment"
                label="Opis treningu"
                type="text"
                fullWidth
                value={comment}
                onChange={handleChangeComment}
                variant="outlined"
              />
            </>
          }          
          {
          !loggedIn && (selectedEvent.commentList.length < 2) &&
            <TextField
              multiline
              autoFocus
              outlined
              margin="normal"
              id="comment"
              label="Odpowiedź użytkownika"
              type="text"
              fullWidth
              value={userComment}
              onChange={handleChangeUserComment}
              variant="outlined"
            />

          }
          {
          (!loggedIn && selectedEvent.commentList.length === 2) &&
            <>
            {!editUserComment ? 
              <DialogContentText>
                <TextField
                  multiline
                  margin="normal"
                  id="comment"
                  label="Odpowiedź użytkownika"
                  type="text"
                  fullWidth
                  value={selectedEvent.commentList[1]?.content}
                  variant="outlined"
                />
              </DialogContentText>  
            :
              <TextField
                multiline
                autoFocus
                outlined
                margin="normal"
                id="comment"
                label="Odpowiedź użytkownika"
                type="text"
                fullWidth
                value={userComment}
                onChange={handleChangeUserComment}
                variant="outlined"
              />
            }
              
            </>
          } 
          {
          (loggedIn && (selectedEvent.commentList.length === 2)) &&
              <DialogContentText>
                <TextField
                  multiline
                  margin="normal"
                  id="comment"
                  label="Odpowiedź użytkownika"
                  type="text"
                  fullWidth
                  value={selectedEvent.commentList[1]?.content}
                  variant="outlined"
                />
              </DialogContentText>
          }
        </DialogContent>
        </Box>
        <DialogActions>
          <Button onClick={handleClose}>Zamknij</Button>
          { loggedIn && selectedEvent.commentList.length > 0 && !editPost && <Button onClick={openEditPost}>Edytuj</Button>}
          { loggedIn && selectedEvent.commentList.length > 0 && editPost && <><Button onClick={handleEditPost}>Zapisz</Button> 
            <Button variant="outlined" color="error" onClick={handleDeletePost}>Usuń</Button> </>}
          { !loggedIn && selectedEvent.commentList.length === 2 && !editUserComment && <Button onClick={openEditUserComment}>Edytuj</Button> }
          { !loggedIn && selectedEvent.commentList.length === 2 && editUserComment && <Button onClick={handleEditUserComment}>Zapisz</Button> }
          { !loggedIn && selectedEvent.commentList.length < 2 && <Button onClick={handleAddUserComment}>Zapisz</Button> }
        </DialogActions>
      </Dialog>
    </div>
    }
  </div>
  )
}

export default BigCalendar;