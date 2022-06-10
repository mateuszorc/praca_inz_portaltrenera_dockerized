package com.example.portaltrenera.service;

import com.example.portaltrenera.model.Comment;
import com.example.portaltrenera.model.Post;
import com.example.portaltrenera.payload.request.PostComment;
import com.example.portaltrenera.repository.CommentRepository;
import com.example.portaltrenera.repository.PostRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
public class PostService {

    public final PostRepository postRepository;
    public final CommentRepository commentRepository;
    private static final Logger LOGGER = LogManager.getLogger(PostService.class);

    public PostService(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }


    public List<Post> getPosts() {
        return postRepository.findAllPosts();
    }

    public List<Post> getUserPosts(Long userId) {
        return postRepository.findAllByUserId(userId);
    }

    public List<Post> getUserPostsAndComments(Long userId) {
        List<Post> userPosts = postRepository.findAllByUserId(userId);
        List<Long> postIds = userPosts.stream()
                .map(Post::getId)
                .collect(Collectors.toList());

        List<Comment> comments = commentRepository.findAllByPostIdIn(postIds);
        userPosts.forEach(post -> post.setCommentList(extractComment(comments, post.getId())));
        return userPosts;
    }

    private List<Comment> extractComment(List<Comment> comments, long id) {
        return comments.stream()
                .filter(comment -> comment.getPostId() == id)
                .collect(Collectors.toList());
    }

    // find by post id
    public Post getSinglePost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Post with id: " + id + " does not exist"));
    }

    // find by user and post id
    public Post getSingleUsersPostWithComments(Long userId, Long postId) {
        return postRepository.findByUserIdAndId(userId, postId);
    }

    public Post addPost(Post post) {
        return postRepository.save(post);
    }

    @Transactional
    public Post addCommentToUsersPost(Long postId, boolean isAdmin, PostComment postComment) {
        Post postWithNewComment = getSinglePost(postId);

        if(postWithNewComment.getCommentList().size() < 2) {
            if (isAdmin) {
                Comment comment = new Comment(postId, postComment.getContent());
                commentRepository.save(comment);
                return postWithNewComment;
            }
            if (!isAdmin && postWithNewComment.getCommentList().size() == 1) {
                Comment comment = new Comment(postId, postComment.getContent());
                commentRepository.save(comment);
                List<Comment> comment1 = postWithNewComment.getCommentList();
                comment1.add(comment);
                postWithNewComment.setCommentList(comment1);
                postWithNewComment.setTrainingDone(true);
                return postWithNewComment;
            } else {
                LOGGER.warn("User tried to add first comment in post.");
                throw new IllegalStateException("User can only add a response comment.");
            }
        } else {
            LOGGER.error("Attempt to add 3 comment to post.");
            throw new IllegalStateException("This post already have two comments, you can't add anymore!!!");
        }

    }

    @Transactional
    public Post editPost(Long postId, boolean isAdmin, PostComment postComment) {
        Post editedPost = getSinglePost(postId);

        if (isAdmin) {
            if (!postComment.getTrainingDay().equals(editedPost.getTrainingDay())) {
                editedPost.setTrainingDay(postComment.getTrainingDay());
                return editedPost;
            } else {
                Comment editedComment = commentRepository.findByPostIdAndId(postId, postComment.getId());
                editedComment.setContent(postComment.getContent());
                commentRepository.save(editedComment);
                editedPost.setTitle(postComment.getTitle());
            }

            return editedPost;
        } else {
            if (!editedPost.getTrainingDay().equals(postComment.getTrainingDay())) {
                editedPost.setTrainingDay(postComment.getTrainingDay());
            }
//            editedPost.setTrainingDone(postComment.isTrainingDone());

        }
        return editedPost;
    }

// sprawdzić czy user id naprawde się przydaje ^|
    @Transactional
    public Post editCommentInPost(Long postId, Long commentId, boolean isAdmin, Comment comment) {
        Post postWithEditedComment = getSinglePost(postId);
        Comment editedComment = commentRepository.findByPostIdAndId(postId, commentId);

        long[] ids = postWithEditedComment.getCommentList()
                .stream()
                .flatMapToLong(comment1 -> LongStream.of(comment1.getId()))
                .toArray();
        boolean adminComment = ids[0] == commentId;

        if (isAdmin && adminComment) {
            editedComment.setContent(comment.getContent());
            commentRepository.save(editedComment);
            return postWithEditedComment;
        } else if (!isAdmin && !adminComment) {
            editedComment.setContent(comment.getContent());
            commentRepository.save(editedComment);
            return postWithEditedComment;
        }
        LOGGER.warn("User tried to edit first comment in post.");
        throw new IllegalStateException("User can only edit a response comment.");
    }

    @Transactional
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    @Transactional
    public void deleteCommentInPost(Long postId, Long id, boolean isAdmin) {
        if (isAdmin) {
            commentRepository.deleteByPostIdAndId(postId, id);
        } else if (!isAdmin) {
            Post postWithEditedComment = getSinglePost(postId);
            long[] ids = postWithEditedComment.getCommentList()
                    .stream()
                    .flatMapToLong(comment1 -> LongStream.of(comment1.getId()))
                    .toArray();
            boolean adminComment = ids[0] == id;
            if (!adminComment) {
                commentRepository.deleteByPostIdAndId(postId, id);
            } else {
                LOGGER.warn("User tried to delete not his comment in post.");
                throw new IllegalStateException("You can not delete this comment - authority is too low");
            }
        }
    }
}
