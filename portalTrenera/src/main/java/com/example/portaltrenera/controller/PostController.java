package com.example.portaltrenera.controller;

import com.example.portaltrenera.dto.PostDto;
import com.example.portaltrenera.dto.PostDtoMapper;
import com.example.portaltrenera.model.Comment;
import com.example.portaltrenera.model.Post;
import com.example.portaltrenera.model.User;
import com.example.portaltrenera.payload.request.PostComment;
import com.example.portaltrenera.service.PostService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class PostController {

    private final PostService postService;
    private static final Logger LOGGER = LogManager.getLogger(PostController.class);

    public PostController(PostService postService) {
        this.postService = postService;
    }

    //admin only
    @GetMapping("/posts")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<PostDto> getPosts() {
        LOGGER.info("Admin accessed method: getPosts()");
        return PostDtoMapper.MapToPostDtos(postService.getPosts());
    }

    @GetMapping("/users/{id}/posts")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public List<Post> getUserPosts(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("USER"))) {
            if (!getAccessUserId(auth).equals(id)) {
                LOGGER.warn(
                        "Unauthorized access attempt - user with id: "
                        + getAccessUserId(auth)
                        + " tried to access user's: "
                        + id
                        + " data!");
                throw new AccessDeniedException("Unauthorized");
            }
            LOGGER.info("User with id " + id + " accessed method: getUserPosts()");
            return postService.getUserPosts(id);
        }
        LOGGER.info("Admin accessed method: getUserPosts()");
        return postService.getUserPosts(id);
    }

    @GetMapping("/users/{id}/posts/comments")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public List<Post> getUserPostsAndComments(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("USER"))) {
            if (!getAccessUserId(auth).equals(id)) {
                LOGGER.warn(
                        "Unauthorized access attempt - user with id: "
                                + getAccessUserId(auth)
                                + " tried to access another user's posts, user's: "
                                + id
                                + " data!");
                throw new AccessDeniedException("Unauthorized");
            }
            LOGGER.info("User with id " + id + " accessed method: getUserPostsAndComments()");
            return postService.getUserPostsAndComments(id);
        }
        LOGGER.info("Admin accessed method: getUserPostsAndComments()");
        return postService.getUserPostsAndComments(id);
    }

    //admin only
    @GetMapping("/posts/{id}") //--------może zmienić na datę później
    @PreAuthorize("hasAuthority('ADMIN')")
    public Post getSinglePostWithComments(@PathVariable Long id) {
        LOGGER.info("Admin accessed method: getSinglePostWithComments()");
        return postService.getSinglePost(id);
    }

    @GetMapping("/users/{userId}/posts/{postId}") //--------może zmienić na datę później
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public Post getSingleUsersPostWithComments(@PathVariable Long userId, @PathVariable Long postId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("USER"))) {
            if (!getAccessUserId(auth).equals(userId)) {
                LOGGER.warn(
                        "Unauthorized access attempt - user with id: "
                                + getAccessUserId(auth)
                                + " tried to access another user's post, user's: "
                                + userId
                                + ", post id: "
                                + postId);
                throw new AccessDeniedException("Unauthorized");
            }
            LOGGER.info("User with id " + userId + " accessed method: getSingleUsersPostWithComments(), post id: "
                    + postId);
            return postService.getSingleUsersPostWithComments(userId, postId);
        }
        LOGGER.info("Admin accessed method: getSingleUsersPostWithComments()");
        return postService.getSingleUsersPostWithComments(userId, postId);
    }

//    admin only
    @PostMapping("/posts")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Post addPostWithComment(@RequestBody PostComment postComment) {
        LOGGER.info("Admin added post - userId: " + postComment.getUserId() + "\ntitle: " + postComment.getTitle()
                + "\ncomment: " + postComment.getContent() + "\n\n");

        Post post = new Post (
                postComment.getUserId(),
                postComment.getTitle(),
                postComment.isTrainingDone(),
                postComment.getTrainingDay()
        );

        Post addedPost = postService.addPost(post);
        Comment comment = new Comment(
                addedPost.getId(),
                postComment.getContent()
        );
        postService.addCommentToUsersPost(addedPost.getId(), true, postComment);
        addedPost.setCommentList(Collections.singletonList(comment));
        return addedPost;
    }

    @PutMapping("/users/{userId}/posts/{postId}/comments")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public Post addCommentToUsersPost(@PathVariable Long userId, @PathVariable Long postId, @RequestBody PostComment postComment) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("USER"))) {
            if (!getAccessUserId(auth).equals(userId)) {
                LOGGER.warn(
                        "Unauthorized access attempt - user with id: "
                                + getAccessUserId(auth)
                                + " tried to add comment to another user's post, user's: "
                                + userId
                                + ", post id: "
                                + postId);
                throw new AccessDeniedException("Unauthorized");
            }
            LOGGER.info("User with id: " + userId + "\nadded comment to post id: "
                    + postId + "\ncontent:\n" + postComment.getContent());
            return postService.addCommentToUsersPost(postId, false, postComment);
        }
        LOGGER.info("Admin added comment to user's post, user_id: " + userId + " post_id: " + postId
                + "\ncontent:\n" + postComment.getContent());
        return postService.addCommentToUsersPost(postId, true, postComment);
    }

    //admin can edit title, user can edit trainingDone and both can edit trainingDay
    @PutMapping("/users/{userId}/posts/{postId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public Post editPost(@PathVariable Long userId, @PathVariable Long postId,@RequestBody PostComment postComment) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("USER"))) {
            if (!getAccessUserId(auth).equals(userId)) {
                LOGGER.warn(
                        "Unauthorized access attempt - user with id: "
                                + getAccessUserId(auth)
                                + " tried to edit another user's post, user's: "
                                + userId
                                + ", post id: "
                                + postId);
                throw new AccessDeniedException("Unauthorized");
            }
            LOGGER.info("User with id " + userId + "\nedited post(id): " + postId
                    + "\ntraining date: " + postComment.getTrainingDay()
                    + "\n training done: " + postComment.isTrainingDone() +"\ncontent:\n" + postComment.getContent());
            return postService.editPost(postId, false, postComment);
        }
        LOGGER.info("Admin edited users(id) " + userId + " post(id): " + postId + "\ntitle:" + postComment.getTitle()
                + "\ntraining date: " + postComment.getTrainingDay()  +"\ncontent:\n" + postComment.getContent());
        return postService.editPost(postId, true, postComment);
    }

    @PutMapping("/users/{userId}/posts/{postId}/comments/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public Post editCommentInPost(@PathVariable Long userId,@PathVariable Long postId, @PathVariable Long id,@RequestBody Comment comment) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("USER"))) {
            if (!getAccessUserId(auth).equals(userId)) {
                LOGGER.warn(
                        "Unauthorized access attempt - user with id: "
                                + getAccessUserId(auth)
                                + " tried to edit another user's comment, user's id: "
                                + userId
                                + ", post id: "
                                + postId
                                + ", comment id: "
                                + id);
                throw new AccessDeniedException("Unauthorized");
            }
            LOGGER.info("User with id " + userId + " edited commeent in post(id): " + postId
                            + ", comment(id): " + id + "\ncontent:\n" + comment.getContent());
            return postService.editCommentInPost(postId, id, false, comment);
        }
        LOGGER.info("Admin edited comment in users(id) " + userId + " post(id): " + postId + ", comment(id): " + id
                + "\ncontent:\n" + comment.getContent());
        return postService.editCommentInPost(postId, id, true, comment);
    }

    //admin only
    @DeleteMapping("/posts/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deletePost(@PathVariable Long id) {
        LOGGER.info("Admin deleted post(id): " + id);
        postService.deletePost(id);
    }


    @DeleteMapping("/users/{userId}/posts/{postId}/comments/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public void deleteCommentInPost(@PathVariable Long userId, @PathVariable Long postId, @PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("USER"))) {
            if (!getAccessUserId(auth).equals(userId)) {
                LOGGER.warn(
                        "Unauthorized access attempt - user with id: "
                                + getAccessUserId(auth)
                                + " tried to delete another user's comment, user's id: "
                                + userId
                                + " post id: "
                                + postId
                                + " comment id: "
                                + id);
                throw new AccessDeniedException("Unauthorized");
            }
            LOGGER.info("User with id " + userId + " deleted comment in post id: " + postId
                    + ", comment id: " + id);
            postService.deleteCommentInPost(postId, id, false);
        }
        LOGGER.info("Admin deleted comment in post(id): " + postId + ", comment(id): " + id);
        postService.deleteCommentInPost(postId, id, true);
    }

    private Long getAccessUserId(Authentication auth) {
        User user = (User) Objects.requireNonNull(auth).getPrincipal();
        return user.getId();
    }
}
