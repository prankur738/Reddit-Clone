package io.mountblue.redditclone.service.impl;

import io.mountblue.redditclone.entity.Bookmark;
import io.mountblue.redditclone.entity.Post;
import io.mountblue.redditclone.entity.User;
import io.mountblue.redditclone.repository.BookmarkRepository;
import io.mountblue.redditclone.repository.PostRepository;
import io.mountblue.redditclone.repository.UserRepository;
import io.mountblue.redditclone.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class BookmarkServiceImpl implements BookmarkService {
    BookmarkRepository bookmarkRepository;
    PostRepository postRepository;
    UserRepository userRepository;
    @Autowired
    public BookmarkServiceImpl(BookmarkRepository bookmarkRepository,
                               PostRepository postRepository,
                               UserRepository userRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void bookmarkPost(Integer postId, String username) {
        Optional<Post> post = postRepository.findById(postId);
        User user = userRepository.findByUsername(username);
        Optional<Bookmark> byUserAndPost = bookmarkRepository.findByUserAndPost(user, post.get());
        if (byUserAndPost.isPresent()) {
            bookmarkRepository.delete(byUserAndPost.get());
        }
        else{
            Bookmark bookmark = new Bookmark();
            bookmark.setPost(post.get());
            bookmark.setUser(user);
            bookmarkRepository.save(bookmark);
        }
    }
}
