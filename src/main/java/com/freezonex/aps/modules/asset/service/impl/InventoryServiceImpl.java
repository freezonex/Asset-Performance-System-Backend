package com.freezonex.aps.modules.asset.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.common.exception.Asserts;
import com.freezonex.aps.modules.asset.Utils.DateUtils;
import com.freezonex.aps.modules.asset.convert.InventoryConvert;
import com.freezonex.aps.modules.asset.dto.*;
import com.freezonex.aps.modules.asset.mapper.InventoryMapper;
import com.freezonex.aps.modules.asset.model.AssetType;
import com.freezonex.aps.modules.asset.model.Inventory;
import com.freezonex.aps.modules.asset.service.AssetService;
import com.freezonex.aps.modules.asset.service.AssetTypeService;
import com.freezonex.aps.modules.asset.service.InventoryService;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * asset inventory 服务实现类
 * </p>
 *
 * @author supos
 * @since 2024-05-07
 */
@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements InventoryService {

    @Resource
    private InventoryConvert inventoryConvert;
    @Resource
    private AssetService assetService;
    @Resource
    private AssetTypeService assetTypeService;

    @Override
    public CommonPage<InventoryListDTO> list(InventoryListReq req) {
        //1.分页查询已有记录品类
        AssetTypeListReq assetTypeListReq = new AssetTypeListReq();
        assetTypeListReq.setPageNum(req.getPageNum());
        assetTypeListReq.setPageSize(req.getPageSize());
        assetTypeListReq.setAssetType(req.getAssetType());
        CommonPage<AssetTypeListDTO> assetTypePage = assetTypeService.list(assetTypeListReq);
        List<Long> assetTypeIds = assetTypePage.getList().stream().map(AssetTypeListDTO::getId).collect(Collectors.toList());
        //2.获取已有品类的记录数据预期时间距离今天最近一条数据
        LambdaQueryWrapper<Inventory> query = new LambdaQueryWrapper<>();
        query.in(Inventory::getAssetTypeId, assetTypeIds);
        query.orderByAsc(Inventory::getAssetTypeId).orderByAsc(Inventory::getExpectedDate);
        List<Inventory> inventoryList = this.list(query);
        Map<Long, Inventory> map = getNearestInventory(inventoryList);
        //3.查询 asset type 的库存
        Map<Long, Long> assetTypeQuantityMap = assetService.queryGroupByAssetType(assetTypeIds, 0);
        Map<Long, Long> assetTypeAllQuantityMap = assetService.queryGroupByAssetType(assetTypeIds, null);
        //4.组装返回结果
        List<InventoryListDTO> resultList = Lists.newArrayList();
        for (AssetTypeListDTO assetTypeListDTO : assetTypePage.getList()) {
            Long assetTypeId = assetTypeListDTO.getId();
            Inventory inventory;
            if (map.containsKey(assetTypeId)) {
                inventory = map.get(assetTypeId);
            } else {
                //初始化一条数据
                inventory = initInventory(assetTypeListDTO);
            }
            Long allQuantity = assetTypeAllQuantityMap.getOrDefault(assetTypeId, 0L);
            int quantity = assetTypeQuantityMap.getOrDefault(assetTypeId, 0L).intValue();
            inventory.setQuantity(quantity);
            InventoryListDTO inventoryListDTO = inventoryConvert.toDTO(inventory);
            if (!allQuantity.equals(0L)) {
                //计算使用率  （总库存-未使用的库存）/总库存
                inventoryListDTO.setUsageRate((new BigDecimal(allQuantity).subtract(new BigDecimal(quantity))).multiply(new BigDecimal(100)).divide(new BigDecimal(allQuantity), 2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + "%");
            } else {
                inventoryListDTO.setUsageRate("0%");
            }
            resultList.add(inventoryListDTO);
        }
        CommonPage<InventoryListDTO> inventoryPage = new CommonPage<>();
        inventoryPage.setPageNum(assetTypePage.getPageNum());
        inventoryPage.setPageSize(assetTypePage.getPageSize());
        inventoryPage.setTotalPage(assetTypePage.getTotalPage());
        inventoryPage.setTotal(assetTypePage.getTotal());
        inventoryPage.setList(resultList);
        return inventoryPage;
    }

    /**
     * 获取距离今天最近的一条记录
     */
    private Map<Long, Inventory> getNearestInventory(List<Inventory> inventoryList) {
        Map<Long, Inventory> map = new HashMap<>();
        Date today = new Date();
        inventoryList.forEach(v -> {
            if (v.getExpectedDate().before(today)) {
                map.put(v.getAssetTypeId(), v);
            } else {
                Inventory inventory = map.get(v.getAssetTypeId());
                if (inventory != null) {
                    if (inventory.getExpectedDate().before(today)) {
                        map.put(v.getAssetTypeId(), v);
                    }
                } else {
                    map.put(v.getAssetTypeId(), v);
                }

            }
        });
        return map;
    }

    private Inventory initInventory(AssetTypeListDTO assetTypeListDTO) {
        Inventory inventory = new Inventory();
        inventory.setId(0L);
        inventory.setAssetTypeId(assetTypeListDTO.getId());
        inventory.setAssetType(assetTypeListDTO.getAssetType());
        inventory.setQuantity(0);
        inventory.setUnit(assetTypeListDTO.getUnit());
        inventory.setUsageRate(0);
        inventory.setSupplierName(assetTypeListDTO.getSupplierName());
        inventory.setExpectedQuantity(0);
        inventory.setCreationTime(new Date());
        inventory.setExpectedDate(new Date());
        inventory.setAi(0);
        return inventory;
    }

    @Override
    public CommonPage<InventoryDetailListDTO> queryByAssetTypeList(InventoryByAssetTypeListReq req) {
        setPageNum(req);
        Page<Inventory> page = new Page<>(req.getPageNum(), req.getPageSize());
        LambdaQueryWrapper<Inventory> query = new LambdaQueryWrapper<>();
        query.eq(Inventory::getAssetTypeId, req.getAssetTypeId());
        query.orderByAsc(Inventory::getExpectedDate);
        Page<Inventory> assetPage = this.getBaseMapper().selectPage(page, query);
        if (assetPage.getRecords().isEmpty()) {
            //初始化一条记录 保持和 列表页面一致
            AssetTypeListDTO assetTypeListDTO = assetTypeService.getByAssetTypeId(req.getAssetTypeId());
            if (assetTypeListDTO != null) {
                assetPage.setRecords(Lists.newArrayList(initInventory(assetTypeListDTO)));
            }
        }
        CommonPage<InventoryDetailListDTO> result = CommonPage.restPage(assetPage, inventoryConvert::toDetailDTO);
        Date today = new Date();
        result.getList().forEach(v -> {
            if (v.getId().equals(req.getCurrentId())) {
                v.setColorType(1);
            } else if (v.getExpectedDate().before(today)) {
                v.setColorType(0);
            } else {
                v.setColorType(2);
            }
        });
        return result;
    }

    private void setPageNum(InventoryByAssetTypeListReq req) {
        if (BooleanUtil.isTrue(req.getDefaultPage()) && Objects.nonNull(req.getCurrentId())) {//需要获取定位到预期分页
            LambdaQueryWrapper<Inventory> query = new LambdaQueryWrapper<>();
            query.eq(Inventory::getAssetTypeId, req.getAssetTypeId());
            query.orderByAsc(Inventory::getExpectedDate);
            query.select(Inventory::getId);
            List<Inventory> list = this.list(query);
            //获取 list 中 对象id 等于 req 中的currentId 的下标
            int index = list.stream().map(Inventory::getId).collect(Collectors.toList()).indexOf(req.getCurrentId());
            if (index > -1) {
                //如果找到 则重置当前页码
                req.setPageNum(index / req.getPageSize() + 1);
            }
        }
    }

    @Override
    public CommonPage<SafetyLevelAssetTypeListDTO> safetyLevelAssetTypeList(InventorySafetyLevelAssetTypeReq req) {
        List<AssetTypeListDTO> safetyLevelAssetType = Lists.newArrayList();
        //查询所有 asset type
        List<AssetTypeListDTO> assetTypeAllList = assetTypeService.allList();
        Map<Long, AssetTypeListDTO> assetTypeMap = assetTypeAllList.stream().collect(Collectors.toMap(AssetTypeListDTO::getId, v -> v));
        //查询 asset type 的库存
        Map<Long, Long> assetTypeQuantityMap = assetService.queryGroupByAssetType(assetTypeMap.keySet(), 0);

        //查询所有 inventory
        List<Inventory> list = this.list();
        //inventory 按照 asset type id 分组
        Map<Long, List<Inventory>> assetTypes = list.stream().collect(Collectors.groupingBy(Inventory::getAssetTypeId));
        //计算 明天的 expected quantity
        Map<Long, Integer> tomorrowQuantityMap = new HashMap<>();
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        assetTypeAllList.forEach(assetType -> {
            List<Inventory> inventoryList = assetTypes.get(assetType.getId());
            List<Inventory> dataList = fillInventory(assetType.getId(), inventoryList);
            for (Inventory inventory : dataList) {
                LocalDate expectedLocalDate = LocalDateTime.ofInstant(inventory.getExpectedDate().toInstant(), ZoneId.systemDefault()).toLocalDate();
                if (expectedLocalDate.isEqual(tomorrow)) {
                    tomorrowQuantityMap.put(assetType.getId(), inventory.getExpectedQuantity());
                    break;
                }
            }
        });

        for (AssetTypeListDTO dto : assetTypeAllList) {
            Integer expectedQuantity = tomorrowQuantityMap.getOrDefault(dto.getId(), 0);
            Long quantity = assetTypeQuantityMap.getOrDefault(dto.getId(), 0L);//默认库存0
            if (quantity >= expectedQuantity) {
                //安全预期的 asset type
                safetyLevelAssetType.add(dto);
            }
        }
        //使用所有符合安全预期的asset type 进行分页查询
        List<Long> collect = safetyLevelAssetType.stream().map(AssetTypeListDTO::getId).collect(Collectors.toList());
        AssetTypeListReq assetTypeListReq = new AssetTypeListReq();
        assetTypeListReq.setPageNum(req.getPageNum());
        assetTypeListReq.setPageSize(req.getPageSize());
        assetTypeListReq.setAssetTypeIds(collect);
        CommonPage<AssetTypeListDTO> data = assetTypeService.list(assetTypeListReq);
        List<SafetyLevelAssetTypeListDTO> records = Lists.newArrayList();
        //组装结果
        for (AssetTypeListDTO dto : data.getList()) {
            SafetyLevelAssetTypeListDTO safetyLevelAssetTypeListDTO = new SafetyLevelAssetTypeListDTO();
            safetyLevelAssetTypeListDTO.setId(dto.getId());
            safetyLevelAssetTypeListDTO.setAssetType(dto.getAssetType());
            safetyLevelAssetTypeListDTO.setIcon(dto.getIcon());
            records.add(safetyLevelAssetTypeListDTO);
        }
        CommonPage<SafetyLevelAssetTypeListDTO> pageResult = new CommonPage<>();
        pageResult.setPageNum(data.getPageNum());
        pageResult.setPageSize(data.getPageSize());
        pageResult.setTotalPage(data.getTotalPage());
        pageResult.setTotal(data.getTotal());
        pageResult.setList(records);
        return pageResult;
    }

    public List<Inventory> fillInventory(Long assetTypeId, List<Inventory> list) {
        LocalDate now = LocalDate.now();
        List<Inventory> result = new ArrayList<>();
        if (CollectionUtil.isEmpty(list)) {
            Inventory inventory = new Inventory();
            inventory.setAssetTypeId(assetTypeId);
            inventory.setExpectedDate(Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            inventory.setExpectedQuantity(0);
            result.add(inventory);
            return result;
        }
        if (list.size() == 1) {
            Instant instant = list.get(0).getExpectedDate().toInstant();
            LocalDate localDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
            if (localDate.equals(now)) {
                //如果只有一条记录并且正好等于今天的日期
                return list;
            }
        }
        list.sort(Comparator.comparing(Inventory::getExpectedDate));
        Date firstDate = list.get(0).getExpectedDate();
        Date lastDate = list.get(list.size() - 1).getExpectedDate();
        Date today = Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());
        if (firstDate.after(today) || lastDate.before(today)) {
            //如果第一条记录的 expected date 大于今天的日期 或者 如果最后一条记录的 expected date 小于今天的日期
            Inventory inventory = new Inventory();
            inventory.setAssetTypeId(assetTypeId);
            inventory.setExpectedDate(Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            inventory.setExpectedQuantity(0);
            list.add(inventory);
        }
        list.sort(Comparator.comparing(Inventory::getExpectedDate));
        for (int i = 1; i < list.size(); i++) {
            //区间填充日期
            result.addAll(fillInventory(list.get(i - 1), list.get(i)));
        }
        result.sort(Comparator.comparing(Inventory::getExpectedDate));
        Set<Date> set = new HashSet<>();
        Iterator<Inventory> iterator = result.iterator();
        while (iterator.hasNext()) {
            Inventory item = iterator.next();
            if (set.contains(item.getExpectedDate())) {
                iterator.remove(); // 删除重复元素
            } else {
                set.add(item.getExpectedDate());
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean create(InventoryCreateReq req) {
        AssetType assetType = assetTypeService.getById(req.getAssetTypeId());
        if (assetType == null) {
            Asserts.fail("asset type not found");
        }
        Inventory inventory = getInventory(req.getAssetTypeId(), req.getExpectedDate());
        if (inventory != null) {
            LambdaUpdateWrapper<Inventory> updateWrapper = new UpdateWrapper<Inventory>().lambda();
            updateWrapper.set(Inventory::getAi, 0);
            updateWrapper.set(Inventory::getExpectedQuantity, req.getExpectedQuantity());
            updateWrapper.eq(Inventory::getId, inventory.getId());
            return this.update(updateWrapper);
        }
        inventory = new Inventory();
        inventory.setAi(0);
        inventory.setAssetTypeId(assetType.getId());
        inventory.setAssetType(assetType.getAssetType());
        inventory.setUnit(assetType.getUnit());
        inventory.setSupplierName(assetType.getSupplierName());
        inventory.setExpectedQuantity(req.getExpectedQuantity());
        inventory.setCreationTime(new Date());
        inventory.setExpectedDate(Date.from(req.getExpectedDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        inventory.setGmtCreate(new Date());
        return this.save(inventory);
    }

    private List<Inventory> fillInventory(Inventory data1, Inventory data2) {
        Date date1 = data1.getExpectedDate();
        Date date2 = data2.getExpectedDate();
        List<Date> dates = DateUtils.findDates(date1, date2);
        if (dates.size() == 2) {
            return Lists.newArrayList(data1, data2);
        }
        //每天增长值
        int num = (data2.getExpectedQuantity() - data1.getExpectedQuantity()) / (dates.size() - 1);
        dates.remove(0);
        dates.remove(dates.size() - 1);
        List<Inventory> result = new ArrayList<>();
        result.add(data1);
        for (int i = 1; i <= dates.size(); i++) {
            Inventory inventory = new Inventory();
            inventory.setAssetTypeId(data1.getAssetTypeId());
            inventory.setExpectedDate(dates.get(i - 1));
            inventory.setExpectedQuantity(data1.getExpectedQuantity() + num * i);
            result.add(inventory);
        }
        result.add(data2);
        return result;
    }

    public CommonPage<SafetyLevelAssetTypeQuantityListDTO> queryAssetTypeQuantity(InventorySafetyLevelAssetTypeReq req) {
        CommonPage<SafetyLevelAssetTypeListDTO> assetTypePage = this.safetyLevelAssetTypeList(req);
        Map<Long, SafetyLevelAssetTypeListDTO> assetTypeMap = assetTypePage.getList().stream().collect(Collectors.toMap(SafetyLevelAssetTypeListDTO::getId, v -> v));
        Map<Long, Long> assetTypeQuantityMap = assetService.queryGroupByAssetType(assetTypeMap.keySet(), 0);
        List<SafetyLevelAssetTypeQuantityListDTO> records = new ArrayList<>();
        assetTypeMap.forEach((k, v) -> {
            Long quantity = assetTypeQuantityMap.getOrDefault(k, 0L);
            SafetyLevelAssetTypeQuantityListDTO dto = new SafetyLevelAssetTypeQuantityListDTO(v, quantity);
            records.add(dto);
        });
        CommonPage<SafetyLevelAssetTypeQuantityListDTO> result = new CommonPage<>();
        result.setPageNum(assetTypePage.getPageNum());
        result.setPageSize(assetTypePage.getPageSize());
        result.setTotalPage(assetTypePage.getTotalPage());
        result.setTotal(assetTypePage.getTotal());
        result.setList(records);
        return result;
    }

    public InventoryChartDataDTO queryChartData(InventoryChartDataReq req) {
        Long assetTypeId = req.getAssetTypeId();
        Date startDate = Date.from(LocalDate.now().minusDays(4).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(LocalDate.now().plusDays(5).atStartOfDay(ZoneId.systemDefault()).toInstant());
        //查询 inventory
        LambdaQueryWrapper<Inventory> query = new LambdaQueryWrapper<>();
        query.in(Inventory::getAssetTypeId, assetTypeId);
        query.orderByAsc(Inventory::getAssetTypeId).orderByAsc(Inventory::getExpectedDate);
        List<Inventory> inventoryList = this.list(query);
        //查询 asset
        List<AssetListDTO> assetList = assetService.queryByAssetTypeId(Lists.newArrayList(assetTypeId));

        //填充 inventory
        List<Inventory> fillInventoryList = fillInventory(assetTypeId, inventoryList);

        //查询日期
        List<Date> dates = DateUtils.findDates(startDate, endDate);

        //计算每天的库存
        Map<LocalDate, Integer> dateQuantityMap = calculateDateQuantity(assetList, dates);

        //每日分组
        Map<LocalDate, Inventory> inventoryDateMap = fillInventoryList.stream().collect(Collectors.toMap(v -> LocalDateTime.ofInstant(v.getExpectedDate().toInstant(), ZoneId.systemDefault()).toLocalDate(), v -> v));
        final String pattern = "MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        List<String> dateFormatList = new ArrayList<>();
        //组装数据
        List<InventoryChartDataDTO.ChartData> chartDataList = new ArrayList<>();
        for (Date date : dates) {
            LocalDate localDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
            InventoryChartDataDTO.ChartData chartData = new InventoryChartDataDTO.ChartData();
            chartData.setQuantity(dateQuantityMap.getOrDefault(localDate, 0));
            if (inventoryDateMap.containsKey(localDate)) {
                chartData.setExpectedQuantity(inventoryDateMap.get(localDate).getExpectedQuantity());
            }

            chartData.setDate(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
            chartDataList.add(chartData);
            dateFormatList.add(sdf.format(date));
        }
        InventoryChartDataDTO inventoryChartDataDTO = new InventoryChartDataDTO();
        inventoryChartDataDTO.setDates(dateFormatList);
        inventoryChartDataDTO.setDataList(chartDataList);
        return inventoryChartDataDTO;
    }

    /**
     * 计算每天库存数量
     *
     * @param assetList 资产列表
     * @param dates     日期区间
     * @return result
     */
    public Map<LocalDate, Integer> calculateDateQuantity(List<AssetListDTO> assetList, List<Date> dates) {
        Map<LocalDate, Integer> dateQuantityMap = new HashMap<>();//存储每天的库存数据
        for (Date date : dates) {
            LocalDate localDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
            Integer num = dateQuantityMap.getOrDefault(localDate, 0);
            if (CollectionUtil.isNotEmpty(assetList)) {
                for (AssetListDTO asset : assetList) {
                    LocalDate createDate = LocalDateTime.ofInstant(asset.getGmtCreate().toInstant(), ZoneId.systemDefault()).toLocalDate();
                    if (localDate.isAfter(createDate)) {//如果资产创建日期再计算日期之前
                        if (asset.getUsedDate() == null || localDate.isBefore(LocalDateTime.ofInstant(asset.getUsedDate().toInstant(), ZoneId.systemDefault()).toLocalDate())) {
                            //未使用 库存+1 或者 已使用，但是再计算你日期之后使用的 库存+1
                            num++;
                        }
                    }
                }
            }

            dateQuantityMap.put(localDate, num);
        }
        return dateQuantityMap;
    }

    private Inventory getInventory(Long assetTypeId, LocalDate expectedDate) {
        LambdaQueryWrapper<Inventory> query = new LambdaQueryWrapper<>();
        query.eq(Inventory::getAssetTypeId, assetTypeId);
        query.eq(Inventory::getExpectedDate, expectedDate);
        return this.getOne(query);
    }

}
