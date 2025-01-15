import { OBJ_TAG_KEYWORDS, OBJ_TAG_NAME } from "../../components/Globals";
import Tag from "./Tag";

const Color = require('color');

interface TagDto {
  toDomain(): Tag;
  toMap(): {};
}


class TagDto {
  id: string;
  text: string;
  keywords: Array<string>;
  color: number;

  constructor(
    id: string = "",
    text: string = "",
    keywords: Array<string> = [],
    color: number = -1,
  ) {
    this.id = id;
    this.text = text;
    this.keywords = keywords;
    this.color = color;
  }
}

TagDto.prototype.toDomain = function() {
  return new Tag(
    this.id,
    this.text,
    Color(this.color),
  );
}

TagDto.prototype.toMap = function() {
  return {
    "id": this.id,
    [OBJ_TAG_NAME]: this.text,
    [OBJ_TAG_KEYWORDS]: this.keywords,
    "color": this.color,
  };
}

export default TagDto;
