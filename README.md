# devops-share-library

yaml化pipeline,自动生成stage

1. 在代码仓库或配置中心中定义一个build.yaml文件 -> 与业务服务1对1绑定

2. 自动渲染生成Stage

3. 自动渲染PodTemplate

4. 支持高玩模式和普通模式 → 既可以用已编写好的kind, 也可以自定义image, 使用script自定义工作流