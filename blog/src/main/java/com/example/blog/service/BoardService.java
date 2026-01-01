package com.example.blog.service;

import java.util.List;
import com.example.blog.dto.BoardDTO;

public interface BoardService {
    List<BoardDTO> getAllBoards();
    BoardDTO getBoardById(int id);
    void insertBoard(BoardDTO board);
    void updateBoard(BoardDTO board);
    void deleteBoard(int id);
    List<BoardDTO> getRecentBoards();

    int countBoards();
    List<BoardDTO> getBoardsByPage(int page, int pageSize);

    // 검색
    int countSearchBoards(String keyword);
    List<BoardDTO> searchBoards(String keyword, int page, int pageSize);

    int countBoardsByCategoryAndKeyword(int categoryId, String keyword);
    List<BoardDTO> searchBoardsByCategoryAndKeyword(int categoryId, String keyword, int page, int pageSize);

    int countBoardsByCategory(int categoryId);
    List<BoardDTO> getBoardsByCategory(int categoryId, int page, int pageSize);

    void incrementViews(int id);
}
