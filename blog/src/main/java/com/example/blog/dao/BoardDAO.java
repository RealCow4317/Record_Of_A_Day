package com.example.blog.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.example.blog.dto.BoardDTO;

public interface BoardDAO {
    List<BoardDTO> getAllBoards();

    BoardDTO getBoardById(int id);

    void insertBoard(BoardDTO board);

    void updateBoard(BoardDTO board);

    void deleteBoard(int id);

    List<BoardDTO> getRecentBoards();

    int countBoards();

    List<BoardDTO> getBoardsByPage(
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    // 검색
    int countSearchBoards(@Param("keyword") String keyword);

    List<BoardDTO> searchBoards(
            @Param("keyword") String keyword,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    int countBoardsByCategoryAndKeyword(
            @Param("categoryId") int categoryId,
            @Param("keyword") String keyword
    );

    List<BoardDTO> searchBoardsByCategoryAndKeyword(
            @Param("categoryId") int categoryId,
            @Param("keyword") String keyword,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    int countBoardsByCategory(@Param("categoryId") int categoryId);

    List<BoardDTO> getBoardsByCategory(
            @Param("categoryId") int categoryId,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    void incrementViews(int id);
}
