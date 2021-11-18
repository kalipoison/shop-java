package com.gohb.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.domain.Notice;
import com.gohb.mapper.NoticeMapper;
import com.gohb.service.NoticeService;
import com.gohb.vo.NoticeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;


    /**
     * 分页查询公告
     *
     * @param page
     * @param notice
     * @return
     */
    @Override
    public IPage<Notice> findNoticePage(Page<Notice> page, Notice notice) {
        return noticeMapper.selectPage(page, new LambdaQueryWrapper<Notice>()
                .eq(notice.getStatus() != null, Notice::getStatus, notice.getStatus())
                .eq(notice.getIsTop() != null, Notice::getIsTop, notice.getIsTop())
                .like(StringUtils.hasText(notice.getTitle()), Notice::getTitle, notice.getTitle())
        );
    }


    @Override
    public boolean save(Notice notice) {
        log.info("新增公告{}", JSON.toJSONString(notice));
        notice.setUpdateTime(new Date());
        if (notice.getStatus().equals(1)) {
            notice.setPublishTime(new Date());
        }
        return super.save(notice);
    }


    /**
     * 加载前台公告
     *
     * @return
     */
    @Override
    public List<NoticeVo> findNoticeVo() {
        List<Notice> notices = noticeMapper.selectList(new LambdaQueryWrapper<Notice>()
                .eq(Notice::getStatus, 1)
                .eq(Notice::getIsTop, 1)
        );
        List<NoticeVo> noticeVos = new ArrayList<>(notices.size() * 2);
        notices.forEach(notice -> {
            NoticeVo noticeVo = new NoticeVo();
            noticeVo.setTitle(notice.getTitle());
            noticeVos.add(noticeVo);
        });
        return noticeVos;
    }
}
