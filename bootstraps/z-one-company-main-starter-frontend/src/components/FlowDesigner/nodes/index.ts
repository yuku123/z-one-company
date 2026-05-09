// 节点定义 - 等 LogicFlow 加载完后在 registerApprovalNodes 里定义

// 注册所有节点
export function registerApprovalNodes(lf: any): void {
  // 开始节点
  class StartNode extends lf.RectNode {
    static extendKey = 'StartNode';

    getNodeStyle() {
      const style = super.getNodeStyle();
      style.fill = '#52c41a';
      style.stroke = '#52c41a';
      style.strokeWidth = 2;
      return style;
    }

    getTextStyle() {
      const style = super.getTextStyle();
      style.color = '#fff';
      style.fontSize = 14;
      return style;
    }

    getShape() {
      const { x, y, width, height } = this.getAttributes();
      const r = height / 2;
      return lf.h('rect', {
        x: x - width / 2,
        y: y - height / 2,
        width,
        height,
        rx: r,
        ry: r,
      });
    }
  }

  // 审批节点
  class ApprovalNode extends lf.RectNode {
    static extendKey = 'ApprovalNode';

    getNodeStyle() {
      const style = super.getNodeStyle();
      style.fill = '#fff';
      style.stroke = '#1677ff';
      style.strokeWidth = 2;
      return style;
    }

    getTextStyle() {
      const style = super.getTextStyle();
      style.color = '#262626';
      style.fontSize = 14;
      return style;
    }
  }

  // 条件节点（菱形）
  class ConditionNode extends lf.PolygonNode {
    static extendKey = 'ConditionNode';

    getNodeStyle() {
      const style = super.getNodeStyle();
      style.fill = '#fff7e6';
      style.stroke = '#fa8c16';
      style.strokeWidth = 2;
      return style;
    }

    getTextStyle() {
      const style = super.getTextStyle();
      style.color = '#262626';
      style.fontSize = 13;
      return style;
    }

    getAttributes() {
      const attributes = super.getAttributes();
      const width = Math.max(80, attributes.width || 80);
      const height = Math.max(80, attributes.height || 80);
      const points = [
        [attributes.x, attributes.y - height / 2],
        [attributes.x + width / 2, attributes.y],
        [attributes.x, attributes.y + height / 2],
        [attributes.x - width / 2, attributes.y],
      ];
      return { ...attributes, points, width, height };
    }
  }

  // 抄送节点
  class CopyNode extends lf.RectNode {
    static extendKey = 'CopyNode';

    getNodeStyle() {
      const style = super.getNodeStyle();
      style.fill = '#f6ffed';
      style.stroke = '#52c41a';
      style.strokeWidth = 2;
      style.strokeDasharray = '5,5';
      return style;
    }

    getTextStyle() {
      const style = super.getTextStyle();
      style.color = '#262626';
      style.fontSize = 14;
      return style;
    }
  }

  // 结束节点
  class EndNode extends lf.CircleNode {
    static extendKey = 'EndNode';

    getNodeStyle() {
      const style = super.getNodeStyle();
      style.fill = '#ff4d4f';
      style.stroke = '#ff4d4f';
      style.strokeWidth = 2;
      return style;
    }

    getTextStyle() {
      const style = super.getTextStyle();
      style.color = '#fff';
      style.fontSize = 14;
      return style;
    }
  }

  lf.register({ type: 'start', view: StartNode, model: lf.RectNode.model });
  lf.register({ type: 'approval', view: ApprovalNode, model: lf.RectNode.model });
  lf.register({ type: 'condition', view: ConditionNode, model: lf.PolygonNode.model });
  lf.register({ type: 'copy', view: CopyNode, model: lf.RectNode.model });
  lf.register({ type: 'end', view: EndNode, model: lf.CircleNode.model });
}
