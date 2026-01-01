package com.example.blog.service;

import java.util.List;

import com.example.blog.dao.BoardDAO;
import com.example.blog.dto.BoardDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private BoardDAO boardDAO;

    @Override
    public List<BoardDTO> getAllBoards() {
        return boardDAO.getAllBoards();
    }

    @Override
    public BoardDTO getBoardById(int id) {
        return boardDAO.getBoardById(id);
    }

    @Override
    public void insertBoard(BoardDTO board) {
        // 썸네일 자동 추출 로직
        String content = board.getContent();
        if (content != null && !content.isEmpty()) {
            Document doc = Jsoup.parse(content);
            Element firstImg = doc.selectFirst("img");
            if (firstImg != null) {
                String src = firstImg.attr("src");
                board.setThumbnail(src);
            }
        }
        boardDAO.insertBoard(board);
    }

    @Override
    public void updateBoard(BoardDTO board) {
        // 수정 시에도 썸네일 다시 추출
        String content = board.getContent();
        if (content != null && !content.isEmpty()) {
            Document doc = Jsoup.parse(content);
            Element firstImg = doc.selectFirst("img");
            if (firstImg != null) {
                String src = firstImg.attr("src");
                board.setThumbnail(src);
            } else {
                board.setThumbnail(null);
            }
        }
        boardDAO.updateBoard(board);
    }

    @Override
    public void deleteBoard(int id) {
        boardDAO.deleteBoard(id);
    }

    @Override
    public List<BoardDTO> getRecentBoards() {
        return boardDAO.getRecentBoards();
    }

    @Override
    public int countBoards() {
        return boardDAO.countBoards();
    }

    @Override
    public List<BoardDTO> getBoardsByPage(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return boardDAO.getBoardsByPage(pageSize, offset);
    }

    @Override
    public int countSearchBoards(String keyword) {
        return boardDAO.countSearchBoards(keyword);
    }

    @Override
    public List<BoardDTO> searchBoards(String keyword, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return boardDAO.searchBoards(keyword, pageSize, offset);
    }

    @Override
    public int countBoardsByCategory(int categoryId) {
        return boardDAO.countBoardsByCategory(categoryId);
    }

    @Override
    public List<BoardDTO> getBoardsByCategory(int categoryId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return boardDAO.getBoardsByCategory(categoryId, pageSize, offset);
    }

    @Override
    public int countBoardsByCategoryAndKeyword(int categoryId, String keyword) {
        return boardDAO.countBoardsByCategoryAndKeyword(categoryId, keyword);
    }

    @Override
    public List<BoardDTO> searchBoardsByCategoryAndKeyword(int categoryId, String keyword, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return boardDAO.searchBoardsByCategoryAndKeyword(categoryId, keyword, pageSize, offset);
    }

    @Override
    public void incrementViews(int id) {
        boardDAO.incrementViews(id);
    }
}
