import { View, Text } from "react-native";
import { useLocalSearchParams } from "expo-router";

export default function PostDetailScreen() {
  const { postId } = useLocalSearchParams<{ postId: string }>();

  return (
    <View className="flex-1 items-center justify-center">
      <Text className="text-lg font-bold">글 상세</Text>
      <Text className="mt-2 text-gray-500">Post ID: {postId}</Text>
    </View>
  );
}
