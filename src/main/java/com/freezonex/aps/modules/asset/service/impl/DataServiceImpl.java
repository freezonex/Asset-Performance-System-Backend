package com.freezonex.aps.modules.asset.service.impl;

import cn.hutool.core.lang.UUID;
import com.freezonex.aps.modules.asset.model.*;
import com.freezonex.aps.modules.asset.service.*;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


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

    @Value("${asset.download.website:http://47.236.10.165:30090}")
    private String website;

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
        sendMqttMsg();
        return true;
    }

    protected void sendMqttMsg(){
        List<Asset> list = assetService.list();
        for (Asset asset : list) {
            assetService.sendMsg(asset);
        }
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
                , "Design", "HealthAndSafety", "QualityAssurance", "EnvironmentalManagement", "PublicRelations"
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
        String specialType = "Freezonex";
        Set<String> strings = assetTypeAndAsset().keySet();
        strings.remove(specialType);
        List<String> assetTypeNames = new ArrayList<>(strings);
        assetTypeNames.add(specialType);//放在最后，页面查询显示再第一行
        String[] supplierBrands = {
                "Apple", "Lenovo", "Samsung", "HP", "Dell", "Acer", "Asus",
                "Intel", "AMD", "NVIDIA", "Cisco", "IBM", "Oracle", "Amazon",
                "Sony", "LG", "Panasonic", "Philips"
        };
        // 定义一个包含5个通用计数单位的字符串数组
        String[] units = {
                "pcs", // Pieces, 件、个
                "doz", // Dozen, 打（12个）
                "dz",  // Another abbreviation for Dozen
                "ctn", // Carton, 箱
                "pkt", // Packet, 小包
        };
        List<AssetType> assetTypes = new ArrayList<>();
        for (String name : assetTypeNames) {
            AssetType assetType = new AssetType();
            assetType.setAssetType(name);
            assetType.setSafetyStockQuantity(0);
            assetType.setUnit(units[new Random().nextInt(units.length)]);
            if (name.equals(specialType)) {
                assetType.setSupplierName(specialType);//需要要求一样
            } else {
                assetType.setSupplierName(supplierBrands[new Random().nextInt(supplierBrands.length)]);
            }
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
        // 定义一个包含20个维护日志描述的字符串数组
        String[] maintenanceLogs = {
                "Replace Filter Cartridge", "Change/Replace Lubricant", "Check/Inspection", "Clean/Cleaning", "Lubricate", "Tighten/Secure", "Calibrate", "Debug/Adjust", "Preventive Maintenance", "Routine Maintenance", "Replace Parts", "Replace Consumables", "Clear Blockage", "Replace Seals", "Performance Testing", "Functional Check", "Fluid Check", "Visual Inspection", "Lubrication System Maintenance", "Electrical System Check"
        };
        List<Maintenance> maintenances = new ArrayList<>();
        List<AssetType> assetTypes = assetTypeService.list();
        for (AssetType assetType : assetTypes) {
            // 获取当前日期
            LocalDate today = LocalDate.now();
            for (int i = -6; i <= 10; i++) {
                LocalDate date = today.minusDays(i);
                Date now = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
                Maintenance maintenance = new Maintenance();
                maintenance.setAssetTypeId(assetType.getId());
                maintenance.setScheduledDate(now);
                maintenance.setContent(maintenanceLogs[new Random().nextInt(maintenanceLogs.length)]);
                if (i >= 0) {
                    int x = new Random().nextInt(4);
                    maintenance.setStatus(x);
                    if (x > 0) {
                        maintenance.setCompletedTime(new Date());
                    }
                } else {
                    maintenance.setStatus(0);
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
        for (int i = -6; i <= 14; i++) {
            LocalDate date = today.minusDays(i);
            Date now = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            for (AssetType assetType : assetTypes) {
                Inventory inventory = new Inventory();
                inventory.setAssetTypeId(assetType.getId());
                inventory.setAssetType(assetType.getAssetType());
                inventory.setUnit(assetType.getUnit());
                inventory.setSupplierName(assetType.getSupplierName());
                //添加部门后理论上随机的库存就多了，这里需要修改
                inventory.setExpectedQuantity(new Random().nextInt(40) + 60);
                inventory.setCreationTime(now);
                inventory.setExpectedDate(now);
                inventory.setGmtCreate(now);
                inventory.setGmtModified(now);
                inventory.setDeleted(0);
                inventory.setAi(new Random().nextInt(4) == 1 ? 1 : 0);//四分之一概率ai标识
                inventories.add(inventory);
            }
        }
        inventoryService.saveBatch(inventories);
    }

    private void initAsset() {
        Map<String, List<AssetComponent>> assetTypeAndAsset = assetTypeAndAsset();
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
            for (Department department : departments) {
                for (AssetType assetType : assetTypes) {
                    List<AssetComponent> list = assetTypeAndAsset.get(assetType.getAssetType());

                    Asset asset = new Asset();
                    asset.setAssetId(String.format("S#%s", num++));
                    AssetComponent assetComponent = list.get(new Random().nextInt(list.size()));
                    asset.setAssetName(assetComponent.getName());
                    asset.setAssetTypeId(assetType.getId());
                    asset.setAssetType(assetType.getAssetType());
                    asset.setVendorModel(String.format("Y%s-%s", new Random().nextInt(901) + 100, new Random().nextInt(10) + 1));
                    asset.setDescription(assetComponent.getDescription());
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
                    if (new Random().nextInt(3) == 1) {
                        assets.add(asset);
                    }
                }
            }
        }

        List<AssetComponent> freezonex = assetTypeAndAsset.get("Freezonex");
        List<String> collect = freezonex.stream().map(AssetComponent::getName).collect(Collectors.toList());
        Iterator<Asset> iterator = assets.iterator();
        List<Asset> lastList1 = new ArrayList<>();//存储Freezonex 每个资产一条记录
        List<Asset> lastList2 = new ArrayList<>();//存储
        while (iterator.hasNext()) {
            Asset item = iterator.next();
            if ("Freezonex".equals(item.getAssetType())) {
                Set<String> set = lastList1.stream().map(Asset::getAssetName).collect(Collectors.toSet());
                if (!set.contains(item.getAssetName())) {
                    lastList1.add(item);
                    collect.remove(item.getAssetName());
                    iterator.remove();
                }
            } else {
                Set<String> set = lastList2.stream().map(Asset::getAssetType).collect(Collectors.toSet());
                if (!set.contains(item.getAssetType())) {
                    lastList2.add(item);
                    iterator.remove();
                }
            }
        }
        assets.addAll(lastList2);
        assets.addAll(lastList1);
        assetService.saveBatch(assets);

        List<Asset> list = assetService.list();
        // 建立名称到URL的映射
        Map<String, String> assetUrlMap = new HashMap<>();
        assetUrlMap.put("Dog", "https://freezonex-aps.oss-ap-southeast-1.aliyuncs.com/Dog_textured_mesh_glb.glb");
        assetUrlMap.put("Table", "https://freezonex-aps.oss-ap-southeast-1.aliyuncs.com/Demo_table_textured_mesh_glb.glb");
        assetUrlMap.put("OMC", "https://freezonex-aps.oss-ap-southeast-1.aliyuncs.com/Omc_textured_mesh_glb.glb");
        assetUrlMap.put("SIS", "https://freezonex-aps.oss-ap-southeast-1.aliyuncs.com/SIS_textured_mesh_glb.glb");

        // 获取随机URL
        String[] randomUrls = {
                "https://freezonex-aps.oss-ap-southeast-1.aliyuncs.com/buster_drone.glb",
                "https://freezonex-aps.oss-ap-southeast-1.aliyuncs.com/free__lamborghini_terzo_millennio.glb",
                "https://freezonex-aps.oss-ap-southeast-1.aliyuncs.com/old_fridge.glb",
                "https://freezonex-aps.oss-ap-southeast-1.aliyuncs.com/stylized_ww1_plane.glb",
                "https://freezonex-aps.oss-ap-southeast-1.aliyuncs.com/free_merc_hovercar.glb",
                "https://freezonex-aps.oss-ap-southeast-1.aliyuncs.com/free_porsche_911_carrera_4s.glb",
                "https://freezonex-aps.oss-ap-southeast-1.aliyuncs.com/mando_helmet.glb",
                "https://freezonex-aps.oss-ap-southeast-1.aliyuncs.com/medieval_chest.glb",
                "https://freezonex-aps.oss-ap-southeast-1.aliyuncs.com/old_paper__cardboard_boxes.glb",
                "https://freezonex-aps.oss-ap-southeast-1.aliyuncs.com/old_tree.glb",
                "https://freezonex-aps.oss-ap-southeast-1.aliyuncs.com/painterly_cottage.glb",
                "https://freezonex-aps.oss-ap-southeast-1.aliyuncs.com/pony_cartoon.glb",
                "https://freezonex-aps.oss-ap-southeast-1.aliyuncs.com/retro-modernized_pip_boy_editable_screen.glb",
                "https://freezonex-aps.oss-ap-southeast-1.aliyuncs.com/shiba.glb",
                "https://freezonex-aps.oss-ap-southeast-1.aliyuncs.com/used_new_balance_574_classic______free.glb"
        };

        // 建立名称到modelUrl的映射
        Map<String, String> modelUrlMap = new HashMap<>();
        modelUrlMap.put("Dog", "https://lumalabs.ai/embed/ade32a6a-9e7b-45d7-bf40-767248a76950?mode=sparkles&background=%23ffffff&color=%23000000&showTitle=true&loadBg=true&logoPosition=bottom-left&infoPosition=bottom-right&cinematicVideo=undefined&showMenu=false");
        modelUrlMap.put("Table", "https://lumalabs.ai/embed/67b5f04c-9f98-4d36-8f37-7688185c1d75?mode=sparkles&background=%23ffffff&color=%23000000&showTitle=true&loadBg=true&logoPosition=bottom-left&infoPosition=bottom-right&cinematicVideo=undefined&showMenu=false");
        modelUrlMap.put("OMC", "https://lumalabs.ai/embed/20e89ef6-42ae-4f31-8398-85b2e1fae5cc?mode=sparkles&background=%23ffffff&color=%23000000&showTitle=true&loadBg=true&logoPosition=bottom-left&infoPosition=bottom-right&cinematicVideo=undefined&showMenu=false");
        modelUrlMap.put("SIS", "https://lumalabs.ai/embed/b2767096-f3f8-4b50-9dea-7b14a2f5bc76?mode=sparkles&background=%23ffffff&color=%23000000&showTitle=true&loadBg=true&logoPosition=bottom-left&infoPosition=bottom-right&cinematicVideo=undefined&showMenu=false");

        Random random = new Random();
        for (Asset asset : list) {
            String assetName = asset.getAssetName();
            // 根据资产名称获取对应的URL
            String url = assetUrlMap.getOrDefault(assetName, randomUrls[random.nextInt(randomUrls.length)]);
            asset.setGlbUrl(url);
            // 设置modelUrl
            if (modelUrlMap.containsKey(assetName)) {
                asset.setModelUrl(modelUrlMap.get(assetName));
            }
        }
        assetService.updateBatchById(list);
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

    private Map<String, List<AssetComponent>> assetTypeAndAsset() {
        Map<String, List<AssetComponent>> assetComponentsWithDescriptions = new HashMap<>();

        assetComponentsWithDescriptions.put("Microchips", Lists.newArrayList(
                new AssetComponent("Integrated Circuits (ICs)", "Miniature electronic circuits used to perform specific functions in devices."),
                new AssetComponent("Capacitors", "Devices that store electrical energy in an electric field."),
                new AssetComponent("Resistors", "Components that oppose the flow of electric current, reducing voltage."),
                new AssetComponent("Transistors", "Semiconductor devices used to amplify or switch electronic signals.")
        ));

        assetComponentsWithDescriptions.put("Steel Beams", Lists.newArrayList(
                new AssetComponent("Welding Rods", "Consumable wire electrodes used in welding to fuse metal parts."),
                new AssetComponent("Reinforcing Bars (Rebars)", "Steel bars used to reinforce concrete and increase its tensile strength."),
                new AssetComponent("Structural Bolts", "High-strength bolts designed for structural applications."),
                new AssetComponent("Anchors", "Fixtures used to secure structures to concrete or masonry.")
        ));

        assetComponentsWithDescriptions.put("Spinning Machines", Lists.newArrayList(
                new AssetComponent("Bobbins", "Hold the yarn during spinning process."),
                new AssetComponent("Spindles", "Rotate to twist fibers into yarn."),
                new AssetComponent("Draw Rolls", "Draw out and align fibers."),
                new AssetComponent("Carding Brushes", "Untangle and clean fibers before spinning.")
        ));

        assetComponentsWithDescriptions.put("Turbines", Lists.newArrayList(
                new AssetComponent("Blades", "Convert fluid flow energy into rotational motion."),
                new AssetComponent("Generator Rotor", "Rotates within magnetic fields to generate electricity."),
                new AssetComponent("Guide Vanes", "Direct the fluid flow onto turbine blades."),
                new AssetComponent("Bearing Assemblies", "Support and reduce friction for rotating parts.")
        ));

        assetComponentsWithDescriptions.put("Warehouse Robots", Lists.newArrayList(
                new AssetComponent("AGV Batteries", "Power source for Automated Guided Vehicles."),
                new AssetComponent("Robotic Arms' End Effectors", "Attach to the arm's end, performing tasks like picking or placing."),
                new AssetComponent("Navigation Sensors", "Enable autonomous navigation within the warehouse."),
                new AssetComponent("Conveyor Belts", "Transport materials between workstations.")
        ));

        assetComponentsWithDescriptions.put("Server Processors", Lists.newArrayList(
                new AssetComponent("Heat Sinks", "Help dissipate heat generated by the processor."),
                new AssetComponent("RAM Modules", "Temporary memory for running applications."),
                new AssetComponent("Hard Disk Drives (HDDs)/Solid State Drives (SSDs)", "Permanent storage for data."),
                new AssetComponent("Power Supplies", "Provide stable power to the server components.")
        ));

        assetComponentsWithDescriptions.put("Elevator Motors", Lists.newArrayList(
                new AssetComponent("Gear Reducers", "Reduce motor speed to increase torque."),
                new AssetComponent("Brake Coils", "Control elevator braking system."),
                new AssetComponent("Hoist Cables", "Lift and lower the elevator car."),
                new AssetComponent("Guide Rails", "Ensure vertical alignment of the elevator car.")
        ));
        assetComponentsWithDescriptions.put("Dental Chairs", Lists.newArrayList(
                new AssetComponent("Dental Handpieces", "High-speed and low-speed rotary tools for drilling and polishing teeth."),
                new AssetComponent("X-ray Tubes", "Emits X-rays for diagnostic imaging of teeth and gums."),
                new AssetComponent("Patient Chair Controls", "Adjust chair position, recline angle, and comfort settings."),
                new AssetComponent("Dental Lights", "Provide bright, focused light to enhance visibility during procedures.")
        ));

        assetComponentsWithDescriptions.put("Broadcasting Cameras", Lists.newArrayList(
                new AssetComponent("Camera Lenses", "Capture high-definition images and video with adjustable zoom and focus."),
                new AssetComponent("Tripods", "Stable support structure to hold the camera steady during filming."),
                new AssetComponent("Microphone Kits", "Professional audio capture devices for clear sound recording."),
                new AssetComponent("Memory Cards", "Fast storage media for recording high-resolution video and images.")
        ));

        assetComponentsWithDescriptions.put("Water Purification Units", Lists.newArrayList(
                new AssetComponent("Reverse Osmosis Membranes", "Filter out impurities by forcing water through a semi-permeable membrane."),
                new AssetComponent("Activated Carbon Filters", "Remove chlorine, odors, and organic compounds."),
                new AssetComponent("UV Sterilization Lamps", "Destroy microorganisms using ultraviolet light."),
                new AssetComponent("Pre-Filters", "Remove large particles and sediment before the main filtration process.")
        ));

        assetComponentsWithDescriptions.put("CNC Milling Machines", Lists.newArrayList(
                new AssetComponent("Cutting Tools (End Mills, Drill Bits)", "Remove material from the workpiece with precision."),
                new AssetComponent("Collet Chucks", "Hold cutting tools securely in place."),
                new AssetComponent("Spindle Motors", "Provide rotational power to the cutting tool."),
                new AssetComponent("Linear Guides", "Enable precise linear motion along machine axes.")
        ));

        assetComponentsWithDescriptions.put("Flight Simulators", Lists.newArrayList(
                new AssetComponent("Control Yokes", "Simulated airplane steering mechanisms."),
                new AssetComponent("Rudder Pedals", "Control simulator yaw movement."),
                new AssetComponent("Visual Display Systems", "Provide a realistic visual representation of flight scenarios."),
                new AssetComponent("Motion Platforms", "Create physical sensations of movement and turbulence.")
        ));

        assetComponentsWithDescriptions.put("Smart Thermostats", Lists.newArrayList(
                new AssetComponent("Temperature Sensors", "Detect ambient temperature for automated control."),
                new AssetComponent("Wi-Fi Modules", "Enable remote control and monitoring via internet."),
                new AssetComponent("Touchscreen Interfaces", "User-friendly display for setting temperature and modes."),
                new AssetComponent("Relay Switches", "Activate heating or cooling systems based on thermostat settings.")
        ));

        assetComponentsWithDescriptions.put("Fuel Injection Systems", Lists.newArrayList(
                new AssetComponent("Fuel Injectors", "Meter and spray fuel into the engine cylinders."),
                new AssetComponent("Fuel Pumps", "Supply fuel under pressure to injectors."),
                new AssetComponent("Fuel Rails", "Distribute pressurized fuel evenly to each injector."),
                new AssetComponent("Pressure Regulators", "Control fuel pressure to optimize injection efficiency.")
        ));

        assetComponentsWithDescriptions.put("Solar Inverters", Lists.newArrayList(
                new AssetComponent("DC-to-AC Converters", "Transform direct current from solar panels to usable alternating current."),
                new AssetComponent("Capacitor Banks", "Store and release energy to stabilize voltage."),
                new AssetComponent("Cooling Fans", "Ensure optimal operating temperature for the inverter electronics."),
                new AssetComponent("Monitoring Units", "Track and report system performance and status.")
        ));

        assetComponentsWithDescriptions.put("CNC Lathes", Lists.newArrayList(
                new AssetComponent("Chuck Jaws", "Hold workpieces securely while they are rotated and machined."),
                new AssetComponent("Live Tooling", "Perform milling and drilling operations while the part is being turned."),
                new AssetComponent("Bar Feeders", "Automatically feed bar stock into the lathe for continuous machining."),
                new AssetComponent("Turret Assemblies", "House multiple cutting tools for quick tool changes during operation.")
        ));

        assetComponentsWithDescriptions.put("POS Terminals", Lists.newArrayList(
                new AssetComponent("Barcode Scanners", "Read product codes for rapid checkout."),
                new AssetComponent("Receipt Printers", "Print transaction records for customers."),
                new AssetComponent("Magnetic Stripe Readers", "Process payments from credit and debit cards."),
                new AssetComponent("PIN Pads", "Securely accept customer PIN entries for card transactions.")
        ));


        assetComponentsWithDescriptions.put("Laboratory Centrifuges", Lists.newArrayList(
                new AssetComponent("Rotors", "Hold sample tubes and spin to separate contents."),
                new AssetComponent("Sample Tubes", "Contain samples for centrifugation."),
                new AssetComponent("Temperature Controllers", "Maintain desired temperatures during spins."),
                new AssetComponent("Safety Lid Locks", "Ensure safe operation by preventing lid opening during spin.")
        ));

//        assetComponentsWithDescriptions.put("Hydraulic Excavators", Lists.newArrayList(
//                new AssetComponent("Hydraulic Cylinders", "Operate the excavator arms and bucket."),
//                new AssetComponent("Bucket Teeth", "Dig and break ground materials."),
//                new AssetComponent("Track Chains", "Provide mobility and stability."),
//                new AssetComponent("Swivel Joints", "Allow rotation of hydraulic lines.")
//        ));

        assetComponentsWithDescriptions.put("AR/VR Headsets", Lists.newArrayList(
                new AssetComponent("OLED/LCD Displays", "High-resolution screens that create immersive visuals for AR/VR experiences."),
                new AssetComponent("Positional Tracking Sensors", "Sensors that track head movements for accurate virtual environment interactions."),
                new AssetComponent("Haptic Feedback Gloves", "Gloves that provide tactile feedback, enhancing the sense of touch in virtual reality."),
                new AssetComponent("Lens Kits", "Optical components that ensure a clear and wide field of view in AR/VR devices.")
        ));

        assetComponentsWithDescriptions.put("Freezonex", Lists.newArrayList(
                new AssetComponent("Dog", "This 3D model shows a \"robot dog\". It has four articulated legs for agile movement, sensors on its head for navigation and environment perception, and a handle on top for easy carrying. The robot is pictured in an office setting, indicating it is used for demonstration or research purposes."),
                new AssetComponent("Table", "This 3D model shows a modern, curved display table with multiple monitors set up on top. The table is metallic and situated in a high-tech environment, possibly an exhibition or a control room, with various electronic equipment and components mounted on the walls in the background."),
                new AssetComponent("OMC", "This 3D model shows a vertical arrangement of industrial electronic modules mounted on a panel. Each module is connected with various cables and has status indicators showing operational conditions. The setup is part of an industrial automation system, designed for process control and monitoring."),
                new AssetComponent("SIS", "This 3D model shows a SUPCON industrial control system rack with multiple modular components. Each module has indicators showing operational status and connectivity, including network interfaces and signal processing units. The system is designed for high reliability and safety in industrial automation applications.")
        ));
        return assetComponentsWithDescriptions;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class AssetComponent {
        private String name;
        private String description;
    }
}
