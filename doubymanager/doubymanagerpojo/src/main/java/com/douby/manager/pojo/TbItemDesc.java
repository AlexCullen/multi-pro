package com.douby.manager.pojo;

import com.douby.common.IDUtils;

import java.util.Date;

public class TbItemDesc {
    private Long itemId;

    private Date created;

    private Date updated;

    private String itemDesc;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc == null ? null : itemDesc.trim();
    }

    public TbItemDesc() {
    }


    public TbItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
        this.created=new Date();
        this.updated = new Date();
    }
}