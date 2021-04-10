const sanitize = item => {
  if (!item.id) {
    item.id = item.url || item.title;
  }
  return item;
};

const prune = item => {
  if (!item.title || (!item.description && !item.content)) {
    return false;
  }
  return true;
};

export default function(items = []) {
  return items.filter(prune).map(sanitize);
}
