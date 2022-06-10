import React, { useState, useRef } from "react";
import AuthService from "../services/auth.service";
import { Navigate, useParams } from "react-router-dom";
import { Box, Button } from "@mui/material";
import Form from "react-validation/build/form";
import Input from "react-validation/build/input";
import CheckButton from "react-validation/build/button";
import UserService from "../services/user.service";

const Profile = () => {

  const required = (value) => {
    if (!value) {
      return (
        <div className="invalid-feedback d-block">
          This field is required!
        </div>
      );
    }
  };

  // const validEmail = (value) => {

//   const validEmail = new RegExp(
//    '^[a-zA-Z0-9._:$!%-]+@[a-zA-Z0-9.-]+.[a-zA-Z]$'
//   );

//   if (!isEmail(value) && !validEmail.test(value)) {
//     return (
//       <div className="invalid-feedback d-block">
//         Email nie jest poprawny.
//       </div>
//     );
//   }
// };

  const vpassword = (value) => {
    if (value) {
      const validPassword = new RegExp('^(?=.*?[A-Za-z])(?=.*?[0-9]).{8,}$');

      if (!validPassword.test(value)) {
        return (
          <div className="invalid-feedback d-block">
            Hasło musi zawierać przynajmniej 8 znaków, jeden znak spacjalny, jedną cyfrę oraz po jednej dużej i małej literze.
          </div>
        );
      }

      if (value !== reEnterPassword) {
         return (
          <div className="invalid-feedback d-block">
            Hasła nie zgadzają się!
          </div>
        );
      }
    }
  };



   const onChangeFirstName = (e) => {
    const firstName = e.target.value;
    setFirstName(firstName);
  };

  const onChangeLastName = (e) => {
    const lastName = e.target.value;
    setLastName(lastName);
  };

  // const onChangeUsername = (e) => {
  //   const username = e.target.value;
  //   setUsername(username);
  // };

  const onChangePassword = (e) => {
    const password = e.target.value;
    setPassword(password);
  };

  const onChangeReEnterPassword = (e) => {
    const password = e.target.value;
    setReEnterPassword(password);
  };

  const form = useRef();
  const checkBtn = useRef();
  const { userId } = useParams();
  const currentUser = AuthService?.getCurrentUser();

  const [edit, setEdit] = useState(false);
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  // const [username, setUsername] = useState(""); //Email
  const [password, setPassword] = useState("");
  const [reEnterPassword, setReEnterPassword] = useState("")
  const [successful, setSuccessful] = useState(false);
  const [message, setMessage] = useState("");
  

  const openEditProfile = () => {
    setEdit(true);
    setFirstName(currentUser.firstName);
    setLastName(currentUser.lastName);
  }

  const closeEditProfile = () => {
    setEdit(false);
    setFirstName("");
    setLastName("");
    setPassword("");
  }

  const handleEditProfile = (e) => {
    e.preventDefault();
    setMessage("");
    setSuccessful(false);
    form.current.validateAll();
    if (checkBtn.current.context._errors.length === 0) {
      UserService.editProfile(firstName, lastName, password, userId)
        .then(
          (response) => {
            setSuccessful(true);
            setMessage("Dane zostały zaaktualizowane, wyloguj się i zaloguj ponownie by zobaczyć zmiany.");

            setTimeout(function() {
              setEdit(false)
              setSuccessful(false)
              setMessage("")
              setFirstName("");
              setLastName("");
              setPassword("");
              setMessage("")  
            }, 4000);   
          },
          (error) => {
            const resMessage =
              (error.response &&
                error.response.data &&
                error.response.data.message) ||
              error.message ||
              error.toString();
            setMessage(resMessage);
            setSuccessful(false);
          }
        );
    }
  }

  const handleDeleteAccount = () => {
    const consent = window.prompt('Jeśli chcesz usunąć konto i wszystkie dane z nim związane wpisz swoje imię i nazwisko.\nTen proces jest nie odwracalny!!!');

    if (consent === `${currentUser.firstName} ${currentUser.lastName}`) {
      UserService.deleteUserAccount(userId);
    }
  }

  return (
    <>
    {currentUser ?
    <>
      { !edit ? 
        <div className="container">
          <header className="jumbotron">
            <h3>
              <strong>{currentUser.firstName}</strong> Profile
            </h3>
          </header>
          {/* <p>
            <strong>Id:</strong> {currentUser.id}
          </p> */}
          <p>
            <strong>Imię:</strong> {currentUser.firstName}
          </p>
          <p>
            <strong>Nazwisko:</strong> {currentUser.lastName}
          </p>
          <p>
            <strong>Email:</strong> {currentUser.email}
          </p>
          {/* <strong>Authorities:</strong>
          <ul className="list-unstyled">
            {currentUser.roles &&
              currentUser.roles.map((role, index) => <li key={index}>{role}</li>)}
          </ul> */}
          <Box className="profile-button-box">
            <Button className="m-1" variant="outlined" onClick={openEditProfile}>Edytuj profil</Button>
            <Button className="m-1" variant="contained" color="error" onClick={handleDeleteAccount}>Usuń konto</Button>
          </Box>
        </div>
      :
        <div className="col-md-12">
          <div className="login-contaioner card card-container">
            <img
              src="/avatar1.png"
              alt="profile-img"
              className="profile-img-card"
            />
            <Form onSubmit={handleEditProfile} ref={form}>
              {!successful && (
                <div>
                  <div className="form-group">
                    <label htmlFor="firstName">Imię</label>
                    <Input
                        type="text"
                        className="form-control"
                        name="firstName"
                        value={firstName}
                        onChange={onChangeFirstName}
                        validations={[required]}
                    />
                  </div>
                  <div className="form-group">
                    <label htmlFor="lastName">Nazwisko</label>
                    <Input
                      type="text"
                      className="form-control"
                      name="lastName"
                      value={lastName}
                      onChange={onChangeLastName}
                      validations={[required]}
                    />
                  </div>
                  {/* <div className="form-group">
                    <label htmlFor="username">Email</label>
                    <Input
                      type="text"
                      className="form-control"
                      name="username"
                      value={username}
                      onChange={onChangeUsername}
                      validations={[required, validEmail]}
                    />
                  </div> */}
                  <div className="form-group">
                    <label htmlFor="password">Hasło</label>
                    <Input
                      type="password"
                      className="form-control"
                      name="password"
                      value={password}
                      onChange={onChangePassword}
                      validations={[vpassword]}
                    />
                  </div>
                  <div className="form-group">
                    <label htmlFor="password">Powtórz hasło</label>
                    <Input
                      type="password"
                      className="form-control"
                      name="reEnterPassword"
                      value={reEnterPassword}
                      onChange={onChangeReEnterPassword}
                    />
                  </div>
                  <div className="form-group">
                    <button className="btn btn-primary btn-block">Edytuj dane</button>
                    <button className="btn btn-primary btn-block" onClick={closeEditProfile}>Anuluj</button>
                  </div>
                </div>
              )}
              {message && (
                <div className="form-group">
                  <div
                    className={
                      successful ? "alert alert-success" : "alert alert-danger"
                    }
                    role="alert"
                  >
                    {message}
                  </div>
                </div>
              )}
              <CheckButton style={{ display: "none" }} ref={checkBtn} />
            </Form>
          </div>
        </div>
      }
    </>
    : <Navigate to="/login" />
    }
    </>
  );
};
export default Profile;
