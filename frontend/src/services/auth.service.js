import axios from "axios";

const API_URL = "http://localhost:3000/api";

const registration = (firstName,lastName,email,password) => {

    const csrf = localStorage.getItem("csrf");
    return axios.post(API_URL + "/registration", {
        firstName,
        lastName,
        email,
        password
    },
        {
            headers: { "X-XSRF-TOKEN" : csrf }
        }
    );
} 

const login = (username, password) => {

    return axios.post(API_URL + "/login", {
        username,
        password
    })
    .then((response) => {
        if (response.data.email) {
            localStorage.setItem("user", JSON.stringify(response.data));
            localStorage.setItem("csrf", `"${response.headers['x-xsrf-token']}"`);
        }
        return response.data;
    })
}

const logout = () => {
    
    localStorage.removeItem("user");
    localStorage.removeItem("csrf");
    return axios.post(API_URL + "/logout")
        .then((response) => {
            return response.data;
        })
}

const getCurrentUser = () => {
    return JSON.parse(localStorage.getItem("user"))
}

const AuthService = {
    registration,
    login,
    logout,
    getCurrentUser
}

export default AuthService;
