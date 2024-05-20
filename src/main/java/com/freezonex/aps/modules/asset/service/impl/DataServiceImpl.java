package com.freezonex.aps.modules.asset.service.impl;

import cn.hutool.core.lang.UUID;
import com.freezonex.aps.modules.asset.model.*;
import com.freezonex.aps.modules.asset.service.*;
import com.google.common.collect.Lists;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


@Service
public class DataServiceImpl implements DataService {

    @Resource
    private AssetTypeService assetTypeService;
    @Resource
    private DepartmentService departmentService;
    @Resource
    private AssetService assetService;
    @Resource
    private InventoryService inventoryService;
    @Resource
    private WorkOrderService workOrderService;
    @Resource
    private MaintenanceService maintenanceService;
    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean initData() {
        clearAll();
        intDepartment();
        initAssetType();
        initAsset();
        initInventory();
        initWorkOrder();
        initMaintenance();
        return true;
    }

    private void clearAll() {
        List<String> tables = Lists.newArrayList("department", "asset_type", "asset", "inventory", "work_order", "maintenance");
        for (String tableName : tables) {
            String sql = "DELETE FROM " + tableName;
            jdbcTemplate.update(sql);
        }
    }

    private void intDepartment() {
        ArrayList<String> strings = Lists.newArrayList("IT", "HR"
                , "Finance", "Marketing", "Sales", "Operations", "Research & Development", "Customer Service", "Legal", "Administration"
        );
        List<Department> departments = new ArrayList<>();
        for (String string : strings) {
            Department department = new Department();
            department.setDepartmentName(string);
            department.setGmtCreate(new Date());
            department.setGmtModified(new Date());
            department.setDeleted(0);
            departments.add(department);
        }
        departmentService.saveBatch(departments);
    }

    private void initAssetType() {
        ArrayList<String> strings = Lists.newArrayList("Computer", "Printer"
                , "Laptop", "Monitor", "Server", "Keyboard", "Mouse", "Scanner", "Projector", "Copier",
                "Telephone", "NetworkRouter", "Switch", "Modem", "UPS", "HardDrive", "RAM", "Motherboard", "CPU", "Speaker"
        );
        List<AssetType> assetTypes = new ArrayList<>();
        for (String string : strings) {
            AssetType assetType = new AssetType();
            assetType.setAssetType(string);
            assetType.setSafetyStockQuantity(0);
            assetType.setUnit("pcs");
            assetType.setSupplierName("apple");
            assetType.setIcon(null);
            assetType.setPriceValue(new Random().nextInt(900) + 100);
            assetType.setGmtCreate(new Date());
            assetType.setGmtModified(new Date());
            assetType.setDeleted(0);
            assetTypes.add(assetType);
        }
        assetTypeService.saveBatch(assetTypes);
    }


    private void initMaintenance() {

        List<Maintenance> maintenances = new ArrayList<>();
        List<AssetType> assetTypes = assetTypeService.list();
        for (AssetType assetType : assetTypes) {
            // 获取当前日期
            LocalDate today = LocalDate.now();
            for (int i = -5; i <= 10; i++) {
                LocalDate date = today.minusDays(i);
                Date now = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
                Maintenance maintenance = new Maintenance();
                maintenance.setAssetTypeId(assetType.getId());
                maintenance.setScheduledDate(now);
                maintenance.setContent("content");
                int x = new Random().nextInt(2);
                maintenance.setStatus(x);
                if (x == 1) {
                    maintenance.setCompletedTime(new Date());
                }
                maintenance.setGmtCreate(new Date());
                maintenance.setGmtModified(new Date());
                maintenance.setDeleted(0);
                maintenances.add(maintenance);
            }
        }

        maintenanceService.saveBatch(maintenances);
    }

    private void initWorkOrder() {
        List<String> orderTypes = new ArrayList<>();
        orderTypes.add("Buy");
        orderTypes.add("Sell");
        orderTypes.add("Market");
        orderTypes.add("Limit");
        orderTypes.add("StopLoss");
        orderTypes.add("TakeProfit");
        List<Department> departments = departmentService.list();
        // 获取当前日期
        LocalDate today = LocalDate.now();
        List<WorkOrder> workOrders = new ArrayList<>();
        int num = 200000;
        int x = 1000;
        for (int i = -5; i <= 10; i++) {
            LocalDate date = today.minusDays(i);
            Date now = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            for (Department department : departments) {
                int y = new Random().nextInt(5);//此部门当天有多少个工单
                for (int j = 0; j < y; j++) {
                    WorkOrder workOrder = new WorkOrder();
                    workOrder.setOrderId(String.format("S#%s", num++));
                    workOrder.setOrderName(orderTypes.get(new Random().nextInt(orderTypes.size())));
                    workOrder.setOrderType(orderTypes.get(new Random().nextInt(orderTypes.size())));
                    workOrder.setDescription(UUID.fastUUID().toString(true));
                    workOrder.setAssetId(String.format("S#%s", x++));
                    workOrder.setPriority(new Random().nextInt(3) + 1);
                    workOrder.setCreationTime(now);
                    workOrder.setDueTime(now);
                    workOrder.setCompletionTime(now);
                    workOrder.setStatus(new Random().nextInt(5) + 1);
                    workOrder.setAssignedTo(department.getId());
                    workOrder.setCreatedBy("admin");
                    workOrder.setNotes("");
                    workOrder.setGmtCreate(now);
                    workOrder.setGmtModified(now);
                    workOrder.setDeleted(0);
                    workOrders.add(workOrder);
                }

            }
        }
        workOrderService.saveBatch(workOrders);
    }

    private void initInventory() {
        List<AssetType> assetTypes = assetTypeService.list();
        List<Inventory> inventories = new ArrayList<>();
        // 获取当前日期
        LocalDate today = LocalDate.now();
        for (int i = -5; i <= 14; i++) {
            LocalDate date = today.minusDays(i);
            Date now = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            for (AssetType assetType : assetTypes) {
                Inventory inventory = new Inventory();
                inventory.setAssetTypeId(assetType.getId());
                inventory.setAssetType(assetType.getAssetType());
                inventory.setUnit(assetType.getUnit());
                inventory.setSupplierName(assetType.getSupplierName());
                inventory.setExpectedQuantity(new Random().nextInt(30) + 35);
                inventory.setCreationTime(now);
                inventory.setExpectedDate(now);
                inventory.setGmtCreate(now);
                inventory.setGmtModified(now);
                inventory.setDeleted(0);
                inventories.add(inventory);
            }
        }
        inventoryService.saveBatch(inventories);
    }

    private void initAsset() {
        String[] person = person();
        List<AssetType> assetTypes = assetTypeService.list();
        List<Department> departments = departmentService.list();
        int num = 1000;
        // 获取当前日期
        LocalDate today = LocalDate.now();
        List<Asset> assets = new ArrayList<>();
        for (int i = 0; i <= 19; i++) {
            LocalDate date = today.minusDays(i);
            Date now = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            for (AssetType assetType : assetTypes) {
                for (Department department : departments) {

                    Asset asset = new Asset();
                    asset.setAssetId(String.format("S#%s", num++));
                    asset.setAssetName(assetType.getAssetType());
                    asset.setAssetTypeId(assetType.getId());
                    asset.setAssetType(assetType.getAssetType());
                    asset.setVendorModel(String.format("Y%s-%s", new Random().nextInt(901) + 100, new Random().nextInt(10) + 1));
                    asset.setDescription("description" + num);
                    asset.setSn(UUID.fastUUID().toString(true));
                    boolean used = new Random().nextInt(5) == 0;
                    asset.setUsedStatus(used ? 1 : 0);
                    if (used) {
                        asset.setUsedDate(now);
                    }
                    //1：Running 2：Maintaining 3：Halt 4：Scheduled Stop
                    asset.setStatus(new Random().nextInt(4) + 1);
                    asset.setDepartmentId(department.getId());
                    asset.setDepartment(department.getDepartmentName());
                    asset.setLocation("A-01-F6");
                    asset.setValue(num + "");
                    asset.setResponsiblePerson(person[num % 20]);
                    asset.setInstallationDate(now);
                    asset.setMaintenanceLog("");
                    asset.setSpareParts("");
                    asset.setDocumentation("");
                    asset.setAttachmentName(null);
                    asset.setAttachmentDir(null);
                    asset.setGmtCreate(now);
                    asset.setGmtModified(now);
                    asset.setDeleted(0);
                    if(new Random().nextInt(3)==1){
                        assets.add(asset);
                    }
                }
            }
        }
        assetService.saveBatch(assets);
    }

    private String[] person() {
        // 定义一个数组，用于存储20个英文名称
        String[] names = new String[20];

        // 初始化数组（这里只是示例，你可以根据需要替换这些名称）
        names[0] = "Alice";
        names[1] = "Bob";
        names[2] = "Charlie";
        names[3] = "David";
        names[4] = "Eva";
        names[5] = "Frank";
        names[6] = "Grace";
        names[7] = "Henry";
        names[8] = "Ivy";
        names[9] = "Jack";
        names[10] = "Kate";
        names[11] = "Liam";
        names[12] = "Mia";
        names[13] = "Noah";
        names[14] = "Olivia";
        names[15] = "Peter";
        names[16] = "Quinn";
        names[17] = "Rachel";
        names[18] = "Sam";
        names[19] = "Tina";
        return names;
    }
}
