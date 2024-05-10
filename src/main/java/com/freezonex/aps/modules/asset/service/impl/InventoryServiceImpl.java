package com.freezonex.aps.modules.asset.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.modules.asset.convert.InventoryConvert;
import com.freezonex.aps.modules.asset.dto.*;
import com.freezonex.aps.modules.asset.mapper.InventoryMapper;
import com.freezonex.aps.modules.asset.model.Inventory;
import com.freezonex.aps.modules.asset.service.AssetService;
import com.freezonex.aps.modules.asset.service.AssetTypeService;
import com.freezonex.aps.modules.asset.service.InventoryService;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
        CommonPage<AssetTypeListDTO> assetTypePage = assetTypeService.list(req, null);
        //2.获取已有品类的记录数据预期时间距离今天最近一条数据
        LambdaQueryWrapper<Inventory> query = new LambdaQueryWrapper<>();
        query.in(Inventory::getAssetTypeId, assetTypePage.getList().stream().map(AssetTypeListDTO::getId).collect(Collectors.toList()));
        query.orderByAsc(Inventory::getAssetTypeId, Inventory::getExpectedDate);
        List<Inventory> inventoryList = this.list(query);
        Map<Long, Inventory> map = getNearestInventory(inventoryList);
        //3.组装返回结果
        List<InventoryListDTO> resultList = Lists.newArrayList();
        for (AssetTypeListDTO assetTypeListDTO : assetTypePage.getList()) {
            Long assetTypeId = assetTypeListDTO.getId();
            if (map.containsKey(assetTypeId)) {
                resultList.add(inventoryConvert.toDTO(map.get(assetTypeId)));
            } else {
                //初始化一条数据
                resultList.add(inventoryConvert.toDTO(initInventory(assetTypeListDTO)));
            }
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
        return inventory;
    }

    @Override
    public CommonPage<InventoryDetailListDTO> queryByAssetTypeList(InventoryByAssetTypeListReq req) {
        Page<Inventory> page = new Page<>(req.getPageNum(), req.getPageSize());
        LambdaQueryWrapper<Inventory> query = new LambdaQueryWrapper<>();
        query.eq(Inventory::getAssetTypeId, req.getAssetTypeId());
        query.orderByAsc(Inventory::getExpectedDate);
        Page<Inventory> assetPage = this.getBaseMapper().selectPage(page, query);
        if (assetPage.getRecords().size() == 0) {
            //初始化一条记录 保持和 列表页面一致
            AssetTypeListDTO assetTypeListDTO = assetTypeService.getByAssetTypeId(req.getAssetTypeId());
            if (assetTypeListDTO != null) {
                assetPage.setRecords(Lists.newArrayList(initInventory(assetTypeListDTO)));
            }
        }
        CommonPage<InventoryDetailListDTO> result = CommonPage.restPage(assetPage, inventoryConvert::toDetailDTO);
        Date today = new Date();
        AtomicInteger num = new AtomicInteger();
        result.getList().forEach(v -> {
            if (v.getExpectedDate().before(today)) {
                v.setColorType(0);
            } else {
                if (num.get() > 0) {
                    v.setColorType(2);
                } else {
                    v.setColorType(1);
                }
                num.getAndIncrement();
            }
        });
        return result;
    }

    @Override
    public CommonPage<SafetyLevelAssetTypeListDTO> safetyLevelAssetTypeList(InventorySafetyLevelAssetTypeReq req) {
        List<AssetTypeListDTO> safetyLevelAssetType = Lists.newArrayList();
        //查询所有 asset type
        List<AssetTypeListDTO> assetTypeAllList = assetTypeService.allList();
        Map<Long, AssetTypeListDTO> assetTypeMap = assetTypeAllList.stream().collect(Collectors.toMap(AssetTypeListDTO::getId, v -> v));
        //查询 asset type 的库存
        Map<Long, Long> assetTypeQuantityMap = assetService.queryGroupByAssetType(assetTypeMap.keySet());

        //查询所有 inventory
        List<Inventory> list = this.list();
        //inventory 按照 asset type id 分组
        Map<Long, List<Inventory>> assetTypes = list.stream().collect(Collectors.groupingBy(Inventory::getAssetTypeId));
        //计算 今天的 expected quantity
        Map<Long, Integer> todayQuantityMap = new HashMap<>();
        LocalDate now = LocalDate.now();
        assetTypeAllList.forEach(assetType -> {
            List<Inventory> inventoryList = assetTypes.get(assetType.getId());
            List<Inventory> dataList = fillInventory(assetType.getId(), inventoryList);
            for (Inventory inventory : dataList) {
                Date expectedDate = inventory.getExpectedDate();
                Calendar instance = Calendar.getInstance();
                instance.setTime(expectedDate);
                LocalDate expectedLocalDate = LocalDate.of(instance.get(Calendar.YEAR), instance.get(Calendar.MONTH) + 1, instance.get(Calendar.DAY_OF_MONTH));
                if (expectedLocalDate.isEqual(now)) {
                    todayQuantityMap.put(assetType.getId(), inventory.getExpectedQuantity());
                    break;
                }
            }
        });

        for (AssetTypeListDTO dto : assetTypeAllList) {
            Integer expectedQuantity = todayQuantityMap.get(dto.getId());
            Long quantity = assetTypeQuantityMap.getOrDefault(dto.getId(), 0L);//默认库存0
            if (quantity >= expectedQuantity) {
                //安全预期的 asset type
                safetyLevelAssetType.add(dto);
            }
        }
        //使用所有符合安全预期的asset type 进行分页查询
        List<Long> collect = safetyLevelAssetType.stream().map(AssetTypeListDTO::getId).collect(Collectors.toList());
        CommonPage<AssetTypeListDTO> data = assetTypeService.list(req, collect);
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

    private List<Inventory> fillInventory(Long assetTypeId, List<Inventory> list) {
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
            if (!localDate.equals(now)) {
                //如果只有一条记录，并且和今天的日期不同
                Inventory inventory = new Inventory();
                inventory.setAssetTypeId(assetTypeId);
                inventory.setExpectedDate(Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                inventory.setExpectedQuantity(0);
                list.add(inventory);
            } else {
                //如果只有一条记录并且正好等于今天的日期
                return list;
            }
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

    private List<Inventory> fillInventory(Inventory data1, Inventory data2) {
        Date date1 = data1.getExpectedDate();
        Date date2 = data2.getExpectedDate();
        List<Date> dates = findDates(date1, date2);
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

    private static List<Date> findDates(Date dBegin, Date dEnd) {
        try {
            List<Date> allDate = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dBegin = sdf.parse(sdf.format(dBegin));
            dEnd = sdf.parse(sdf.format(dEnd));

            allDate.add(dBegin);
            Calendar calBegin = Calendar.getInstance();
            // 使用给定的 Date 设置此 Calendar 的时间
            calBegin.setTime(dBegin);

            // 测试此日期是否在指定日期之后
            while (dEnd.after(calBegin.getTime())) {
                calBegin.add(Calendar.DAY_OF_MONTH, 1);
                allDate.add(calBegin.getTime());
            }
            return allDate;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CommonPage<SafetyLevelAssetTypeQuantityListDTO> queryAssetTypeQuantity(InventorySafetyLevelAssetTypeReq req) {
        CommonPage<SafetyLevelAssetTypeListDTO> assetTypePage = this.safetyLevelAssetTypeList(req);
        Map<Long, SafetyLevelAssetTypeListDTO> assetTypeMap = assetTypePage.getList().stream().collect(Collectors.toMap(SafetyLevelAssetTypeListDTO::getId, v -> v));
        Map<Long, Long> assetTypeQuantityMap = assetService.queryGroupByAssetType(assetTypeMap.keySet());
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
        //更具日期范围查询 inventory
        LambdaQueryWrapper<Inventory> query = new LambdaQueryWrapper<>();
        query.in(Inventory::getAssetTypeId, assetTypeId);
        query.orderByAsc(Inventory::getAssetTypeId, Inventory::getExpectedDate);
        List<Inventory> inventoryList = this.list(query);
        //填充 inventory
        List<Inventory> dataList = fillInventory(assetTypeId, inventoryList);
        //每日分组
        Map<LocalDate, Inventory> inventoryDateMap = dataList.stream().collect(Collectors.toMap(v -> LocalDateTime.ofInstant(v.getExpectedDate().toInstant(), ZoneId.systemDefault()).toLocalDate(), v -> v));

        //查询 资产 计算每天的库存
        List<AssetListDTO> assetList = assetService.queryByAssetTypeId(assetTypeId);
        Map<LocalDate, Integer> map = new HashMap<>();//存储每天的库存数据
        List<LocalDate> localDates = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        final String pattern = "MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        for (Date date : findDates(startDate, endDate)) {
            LocalDate localDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
            Integer num = map.getOrDefault(localDate, 0);
            for (AssetListDTO asset : assetList) {
                LocalDate createDate = LocalDateTime.ofInstant(asset.getGmtCreate().toInstant(), ZoneId.systemDefault()).toLocalDate();
                if (localDate.isAfter(createDate)) {//如果资产创建日期再计算日期之前
                    if (asset.getUsedDate() == null || localDate.isBefore(LocalDateTime.ofInstant(asset.getUsedDate().toInstant(), ZoneId.systemDefault()).toLocalDate())) {
                        //未使用 库存+1 或者 已使用，但是再计算你日期之后使用的 库存+1
                        num++;
                    }
                }
            }
            map.put(localDate, num);
            localDates.add(localDate);
            dates.add(sdf.format(date));
        }
        //组装数据
        List<InventoryChartDataDTO.ChartData> chartDataList = new ArrayList<>();
        for (LocalDate localDate : localDates) {
            InventoryChartDataDTO.ChartData chartData = new InventoryChartDataDTO.ChartData();
            chartData.setQuantity(map.getOrDefault(localDate, 0));
            if (inventoryDateMap.containsKey(localDate)) {
                chartData.setExpectedQuantity(inventoryDateMap.get(localDate).getExpectedQuantity());
            }
            chartData.setDate(localDate.format(DateTimeFormatter.ofPattern(pattern)));
            chartDataList.add(chartData);
        }
        InventoryChartDataDTO inventoryChartDataDTO = new InventoryChartDataDTO();
        inventoryChartDataDTO.setDates(dates);
        inventoryChartDataDTO.setDataList(chartDataList);
        return inventoryChartDataDTO;
    }

}
