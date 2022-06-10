import axios from "axios";
import AuthService from "./auth.service";

const API_URL = "http://localhost:3000/api";
const currentUser = AuthService.getCurrentUser();

const getUserView = () => {
   return axios.get(API_URL + "/users/" + currentUser.id + "/posts/comments");
//    .then(response => {console.log(response.data)})
}

const getAdminView = () => {
    return axios.get(API_URL + "/users/");
}

const getUser = (id) => {
    return axios.get(API_URL + "/users/" + id);
}

const getUserCallendar = (id) => {
    return axios.get(API_URL + "/users/" + id + "/posts/comments")
}

const postAdminPost = (props) => {
 
        const csrf = localStorage.getItem("csrf");

        return axios.post(API_URL + "/posts", {
            id: props.id,
            userId: props.userId,
            title: props.title,
            trainingDone: props.trainingDone,
            trainingDay: props.trainingDay,
            commentList: props.commentList,
            content:props.content
        },
        {
            headers: { "X-XSRF-TOKEN" : csrf }
        }
        )
}

const postUserComment = (props) => {

    const csrf = localStorage.getItem("csrf");
    return axios.put(API_URL + "/users/" + props.userId + "/posts/" + props.postId + "/comments", {
        content: props.content,
        trainingDone: props.trainingDone
    },
        {
            headers: { "X-XSRF-TOKEN" : csrf }
        }
    )
}

const editProfile = (firstName, lastName, password, userId) => {

    const csrf = localStorage.getItem("csrf");
    return axios.put(API_URL + "/users/" + userId, {
    firstName: firstName,
    lastName: lastName,
    password: password
    },
        {
            headers: { "X-XSRF-TOKEN" : csrf }
        }
    )
}

const editPost = (props) => {

    const csrf = localStorage.getItem("csrf");

    return axios.put(API_URL + "/users/" + props.userId + "/posts/" + props.postId, {
    id: props.id,
    title: props.title,
    content: props.content,
    trainingDay: props.trainingDay
    },
        {
            headers: { "X-XSRF-TOKEN" : csrf }
        }
    )
}

const editUserComment = (props) => {

    const csrf = localStorage.getItem("csrf");

    return axios.put(`${API_URL}/users/${props.userId}/posts/${props.postId}/comments/${props.commentId}`, {
        content: props.content
    },
        {
            headers: { "X-XSRF-TOKEN" : csrf }
        }
    )
}

const deletePost = (id) => {

    const csrf = localStorage.getItem("csrf");

    return axios.delete(API_URL + "/posts/" + id, 
        {
            headers: { "X-XSRF-TOKEN" : csrf }
        }
    );
}

const deleteUserAccount = (userId) => {

    const csrf = localStorage.getItem("csrf");

    axios.delete(API_URL + "/users/" + userId,
        {
            headers: { "X-XSRF-TOKEN" : csrf }
        }
    );
    localStorage.removeItem("user");
    return axios.post(API_URL + "/logout")
        .then((response) => {
            return response.data;
        })
}

const adminDeleteUserAccount = (userId) => {

    const csrf = localStorage.getItem("csrf");

    return axios.delete(API_URL + "/users/" + userId, 
        {
            headers: { "X-XSRF-TOKEN" : csrf }
        }
    );
}

const UserService = {
    getUserView,
    getAdminView,
    getUserCallendar,
    getUser,
    postAdminPost,
    postUserComment,
    editProfile,
    deleteUserAccount,
    adminDeleteUserAccount,
    editUserComment,
    editPost,
    deletePost
}

export default UserService;