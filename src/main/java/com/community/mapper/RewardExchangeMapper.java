package com.community.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.entity.RewardExchange;

@Mapper
public interface RewardExchangeMapper extends BaseMapper<RewardExchange> {
    
    @Select("SELECT * FROM tb_reward_exchange WHERE user_id = #{userId} ORDER BY create_time DESC")
    java.util.List<RewardExchange> selectByUserId(@Param("userId") Long userId);
}
