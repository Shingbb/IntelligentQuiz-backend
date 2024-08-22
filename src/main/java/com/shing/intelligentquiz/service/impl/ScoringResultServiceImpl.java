package com.shing.intelligentquiz.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shing.intelligentquiz.common.ErrorCode;
import com.shing.intelligentquiz.constant.CommonConstant;
import com.shing.intelligentquiz.exception.ThrowUtils;
import com.shing.intelligentquiz.mapper.ScoringResultMapper;
import com.shing.intelligentquiz.model.dto.scoringresult.ScoringResultQueryRequest;
import com.shing.intelligentquiz.model.entity.App;
import com.shing.intelligentquiz.model.entity.ScoringResult;
import com.shing.intelligentquiz.model.vo.ScoringResultVO;
import com.shing.intelligentquiz.service.AppService;
import com.shing.intelligentquiz.service.ScoringResultService;
import com.shing.intelligentquiz.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评分结果服务实现类，继承自ServiceImpl基类，提供针对ScoringResult实体的CRUD操作。
 */
@Slf4j
@Service
public class ScoringResultServiceImpl extends ServiceImpl<ScoringResultMapper, ScoringResult> implements ScoringResultService {

    @Resource
    private AppService appService;

    /**
     * 校验评分结果信息的合法性。
     *
     * @param scoringResult 待校验的评分结果实体
     * @param add           指示当前操作是添加还是修改
     * @throws RuntimeException 如果校验失败，抛出运行时异常
     */
    @Override
    public void validScoringResult(ScoringResult scoringResult, boolean add) {
        ThrowUtils.throwIf(scoringResult == null, ErrorCode.PARAMS_ERROR);
        // 从对象中提取关键信息
        String resultName = scoringResult.getResultName();
        Long appId = scoringResult.getAppId();
        // 添加操作时的校验规则
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(resultName), ErrorCode.PARAMS_ERROR, "结果名称不能为空");
            ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "appId 非法");
        }
        // 修改操作时的校验规则
        if (StringUtils.isNotBlank(resultName)) {
            ThrowUtils.throwIf(resultName.length() > 128, ErrorCode.PARAMS_ERROR, "结果名称不能超过 128");
        }
        if (appId != null) {
            App app = appService.getById(appId);
            ThrowUtils.throwIf(app == null, ErrorCode.PARAMS_ERROR, "应用不存在");
        }
    }

    /**
     * 根据查询请求构建查询条件。
     *
     * @param scoringResultQueryRequest 查询请求对象，包含各种过滤条件和排序规则
     * @return 构建好的查询条件对象
     */
    @Override
    public QueryWrapper<ScoringResult> getQueryWrapper(ScoringResultQueryRequest scoringResultQueryRequest) {
        QueryWrapper<ScoringResult> queryWrapper = new QueryWrapper<>();
        if (scoringResultQueryRequest == null) {
            return queryWrapper;
        }
        // 从查询请求对象中提取过滤条件
        Long id = scoringResultQueryRequest.getId();
        String resultName = scoringResultQueryRequest.getResultName();
        String resultDesc = scoringResultQueryRequest.getResultDesc();
        String resultPicture = scoringResultQueryRequest.getResultPicture();
        String resultProp = scoringResultQueryRequest.getResultProp();
        Integer resultScoreRange = scoringResultQueryRequest.getResultScoreRange();
        Long appId = scoringResultQueryRequest.getAppId();
        Long userId = scoringResultQueryRequest.getUserId();
        Long notId = scoringResultQueryRequest.getNotId();
        String searchText = scoringResultQueryRequest.getSearchText();
        String sortField = scoringResultQueryRequest.getSortField();
        String sortOrder = scoringResultQueryRequest.getSortOrder();

        // 根据提取的条件构建查询Wrapper
        // 多字段搜索
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("resultName", searchText).or().like("resultDesc", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(resultName), "resultName", resultName);
        queryWrapper.like(StringUtils.isNotBlank(resultDesc), "resultDesc", resultDesc);
        queryWrapper.like(StringUtils.isNotBlank(resultProp), "resultProp", resultProp);
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(appId), "appId", appId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(resultScoreRange), "resultScoreRange", resultScoreRange);
        queryWrapper.eq(StringUtils.isNotBlank(resultPicture), "resultPicture", resultPicture);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 将ScoringResult实体转换为ScoringResultVO对象，用于前端展示。
     *
     * @param scoringResult 待转换的评分结果实体
     * @param request       HTTP请求对象，可能用于获取额外的上下文信息
     * @return 转换后的ScoringResultVO对象
     */
    @Override
    public ScoringResultVO getScoringResultVO(ScoringResult scoringResult, HttpServletRequest request) {
        // 使用工具类进行实体到VO的转换
        ScoringResultVO scoringResultVO = ScoringResultVO.objToVo(scoringResult);
        // 可选操作：根据需求为VO补充额外的信息
        return scoringResultVO;
    }

    /**
     * 分页获取评分结果，将ScoringResult实体列表转换为ScoringResultVO列表。
     *
     * @param scoringResultPage 分页对象，包含当前页的评分结果实体列表
     * @param request           HTTP请求对象，可能用于获取额外的上下文信息
     * @return 分页对象，包含当前页的ScoringResultVO列表
     */
    @Override
    public Page<ScoringResultVO> getScoringResultVOPage(Page<ScoringResult> scoringResultPage, HttpServletRequest request) {
        List<ScoringResult> scoringResultList = scoringResultPage.getRecords();
        Page<ScoringResultVO> scoringResultVOPage = new Page<>(scoringResultPage.getCurrent(), scoringResultPage.getSize(), scoringResultPage.getTotal());
        if (CollUtil.isEmpty(scoringResultList)) {
            return scoringResultVOPage;
        }
        // 实体列表转VO列表
        List<ScoringResultVO> scoringResultVOList = scoringResultList.stream().map(ScoringResultVO::objToVo).collect(Collectors.toList());

        // 可选操作：为VO列表补充额外信息

        scoringResultVOPage.setRecords(scoringResultVOList);
        return scoringResultVOPage;
    }
}
