import React, { useState, useRef } from "react";
import Form from "react-validation/build/form";
import Input from "react-validation/build/input";
import CheckButton from "react-validation/build/button";
import { isEmail } from "validator";
import AuthService from "../services/auth.service";
import { Navigate } from "react-router-dom";

const adminLoggedIn = AuthService.getCurrentUser()?.id

const required = (value) => {
  if (!value) {
    return (
      <div className="invalid-feedback d-block">
        This field is required!
      </div>
    );
  }
};

const validEmail = (value) => {

  const validEmail = new RegExp(
   '^[a-zA-Z0-9._:$!%-]+@[a-zA-Z0-9.-]+.[a-zA-Z]$'
  );

  if (!isEmail(value) && !validEmail.test(value)) {
    return (
      <div className="invalid-feedback d-block">
        Email nie jest poprawny.
      </div>
    );
  }
};

const vpassword = (value) => {

const validPassword = new RegExp('^(?=.*?[A-Za-z])(?=.*?[0-9]).{8,}$');

  if (!validPassword.test(value)) {
    return (
      <div className="invalid-feedback d-block">
        Hasło musi zawierać przynajmniej 8 znaków, jeden znak spacjalny, jedną cyfrę oraz po jednej dużej i małej literze.
      </div>
    );
  }
};

const Registration = (props) => {
  const form = useRef();
  const checkBtn = useRef();
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [successful, setSuccessful] = useState(false);
  const [message, setMessage] = useState("");

  const onChangeFirstName = (e) => {
    const firstName = e.target.value;
    setFirstName(firstName);
  };

  const onChangeLastName = (e) => {
    const lastName = e.target.value;
    setLastName(lastName);
  };

  const onChangeUsername = (e) => {
    const username = e.target.value;
    setUsername(username);
  };

  const onChangePassword = (e) => {
    const password = e.target.value;
    setPassword(password);
  };

  const handleRegistration = (e) => {
    e.preventDefault();
    setMessage("");
    setSuccessful(false);
    form.current.validateAll();
    if (checkBtn.current.context._errors.length === 0) {
      AuthService.registration(firstName, lastName, username, password).then(
        (response) => {
          setMessage(response.data);
          setSuccessful(true);

          setTimeout(function() {
            setSuccessful(false);
            setMessage("");
            setFirstName("");
            setLastName("");
            setUsername("");
            setPassword("");
          }, 4000)
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
  };

  return (
    <>
    {adminLoggedIn === 1 ? 
        <div className="col-md-12">
      <div className="login-contaioner card card-container">
        <img
          src="/avatar1.png"
          alt="profile-img"
          className="profile-img-card"
        />
        <Form onSubmit={handleRegistration} ref={form}>
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
              <div className="form-group">
                <label htmlFor="username">Email</label>
                <Input
                  type="text"
                  className="form-control"
                  name="username"
                  value={username}
                  onChange={onChangeUsername}
                  validations={[required, validEmail]}
                />
              </div>
              <div className="form-group">
                <label htmlFor="password">Hasło</label>
                <Input
                  type="password"
                  className="form-control"
                  name="password"
                  value={password}
                  onChange={onChangePassword}
                  validations={[required, vpassword]}
                />
              </div>
              <div className="form-group">
                <button className="btn btn-primary btn-block">Zarejestruj</button>
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
    :
    <Navigate to="/login" />
    }
    </>
  );
};
export default Registration;
