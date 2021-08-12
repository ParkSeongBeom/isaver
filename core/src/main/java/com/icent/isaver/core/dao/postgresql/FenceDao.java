package com.icent.isaver.core.dao.postgresql;

import com.icent.isaver.core.bean.FenceBean;

import java.util.List;

public interface FenceDao {
    List<FenceBean> findListCamera(FenceBean fenceBean);
    FenceBean findByFence(FenceBean fenceBean);
}
