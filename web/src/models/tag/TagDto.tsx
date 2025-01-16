import { OBJ_TAG_KEYWORDS, OBJ_TAG_NAME } from "../../components/Globals";
import Tag from "./Tag";

interface TagDto {
  toDomain(): Tag;
  toMap(): {};
}


class TagDto {
  id: string;
  text: string;
  keywords: string[];
  color: number;

  constructor(
    id: string = "",
    text: string = "",
    keywords: string[] = [],
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
  );
}

TagDto.prototype.toMap = function() {
  return {
    "id": this.id,
    [OBJ_TAG_NAME]: this.text,
    [OBJ_TAG_KEYWORDS]: this.keywords,
    "color": -1,
  };
}

export default TagDto;
